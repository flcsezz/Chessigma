package com.chessigma.app.engine

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@Singleton
class StockfishEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var process: Process? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null

    private val mutex = Mutex()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isExtracting = MutableStateFlow(false)
    val isExtracting: StateFlow<Boolean> = _isExtracting.asStateFlow()

    private val _extractionProgress = MutableStateFlow(0f)
    val extractionProgress: StateFlow<Float> = _extractionProgress.asStateFlow()

    suspend fun initialise(): Boolean = withContext(Dispatchers.IO) {
        val binaryPath = getBinaryPath() ?: return@withContext false
        val binaryFile = File(binaryPath)

        if (!binaryFile.exists()) {
            Timber.e("Stockfish binary not found at $binaryPath")
            return@withContext false
        }

        mutex.withLock {
            if (_isReady.value) return@withContext true
            try {
                process = Runtime.getRuntime().exec(binaryPath)
                writer = BufferedWriter(OutputStreamWriter(process?.outputStream))
                reader = BufferedReader(InputStreamReader(process?.inputStream))

                sendCommand("uci")
                val result = withTimeoutOrNull(5000) {
                    var line: String?
                    while (reader?.readLine().also { line = it } != null) {
                        if (line == "uciok") {
                            _isReady.value = true
                            Timber.i("StockfishEngine: Initialized successfully")
                            return@withTimeoutOrNull true
                        }
                    }
                    false
                }
                if (result == true) return@withContext true
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize Stockfish engine")
            }
        }
        false
    }

    private fun getBinaryPath(): String? {
        val nativeLibDir = context.applicationInfo.nativeLibraryDir
        val binaryFile = File(nativeLibDir, "libstockfish.so")
        return if (binaryFile.exists()) {
            binaryFile.absolutePath
        } else {
            Timber.e("Stockfish binary not found in $nativeLibDir")
            null
        }
    }


    suspend fun setOption(name: String, value: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            sendCommand("setoption name $name value $value")
        }
    }

    suspend fun setPosition(fen: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            sendCommand("position fen $fen")
        }
    }

    suspend fun setSkillLevel(level: Int) = withContext(Dispatchers.IO) {
        val boundedLevel = level.coerceIn(0, 20)
        mutex.withLock {
            sendCommand("setoption name Skill Level value $boundedLevel")
        }
    }

    suspend fun getBestMove(depth: Int): String = withContext(Dispatchers.IO) {
        mutex.withLock {
            sendCommand("go depth $depth")
            var bestMove = ""
            withTimeoutOrNull(10000) {
                var line: String?
                while (reader?.readLine().also { line = it } != null) {
                    if (line?.startsWith("bestmove") == true) {
                        bestMove = line?.split(" ")?.getOrNull(1) ?: ""
                        break
                    }
                }
            }
            bestMove
        }
    }

    suspend fun evaluate(fen: String, depth: Int): Int = withContext(Dispatchers.IO) {
        mutex.withLock {
            sendCommand("position fen $fen")
            sendCommand("go depth $depth")
            var cp = 0
            var infoLine = ""
            withTimeoutOrNull(10000) {
                var line: String?
                while (reader?.readLine().also { line = it } != null) {
                    if (line?.startsWith("info depth $depth ") == true) {
                        infoLine = line ?: ""
                    }
                    if (line?.startsWith("bestmove") == true) {
                        break
                    }
                }
            }
            
            // Parse the info line for score cp
            val parts = infoLine.split(" ")
            val cpIndex = parts.indexOf("cp")
            if (cpIndex != -1 && cpIndex + 1 < parts.size) {
                cp = parts[cpIndex + 1].toIntOrNull() ?: 0
            }
            
            // Stockfish evaluation is relative to the side to move
            val isBlackTurn = fen.split(" ").getOrNull(1) == "b"
            if (isBlackTurn) -cp else cp
        }
    }

    fun shutdown() {
        try {
            sendCommand("quit")
            writer?.close()
            reader?.close()
            process?.destroy()
        } catch (e: Exception) {
            // Ignore termination errors
        }
    }

    private fun sendCommand(command: String) {
        writer?.apply {
            write("$command\n")
            flush()
        }
    }
}
