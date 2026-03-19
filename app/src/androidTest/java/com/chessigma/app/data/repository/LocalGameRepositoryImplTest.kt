package com.chessigma.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chessigma.app.data.local.AppDatabase
import com.chessigma.app.domain.model.ChessMove
import com.chessigma.app.domain.model.PieceType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalGameRepositoryImplTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: LocalGameRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = LocalGameRepositoryImpl(db.gameDao(), db.moveDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun createGame_then_loadGame_roundTrip() = runBlocking {
        val gameId = "test-game-001"
        val startedAt = System.currentTimeMillis()

        repository.createGame(gameId, startedAt)

        val result = repository.loadGame(gameId)
        assertNotNull("Game should be persisted and retrievable", result)
        val (entity, moves) = result!!
        assertEquals(gameId, entity.id)
        assertEquals("LOCAL", entity.platform)
        assertEquals("*", entity.result)
        assertEquals(0, moves.size)
    }

    @Test
    fun saveMove_persists_correct_ply_order() = runBlocking {
        val gameId = "test-game-002"
        repository.createGame(gameId, System.currentTimeMillis())

        val startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        val moves = listOf(
            ChessMove("e2", "e4", san = "e4"),
            ChessMove("e7", "e5", san = "e5"),
            ChessMove("g1", "f3", san = "Nf3")
        )

        moves.forEachIndexed { ply, move ->
            repository.saveMove(gameId, ply, move, startFen)
        }

        val loaded = repository.loadGame(gameId)
        assertNotNull(loaded)
        val savedMoves = loaded!!.second
        assertEquals(3, savedMoves.size)
        assertEquals(0, savedMoves[0].ply)
        assertEquals(1, savedMoves[1].ply)
        assertEquals(2, savedMoves[2].ply)
        assertEquals("e2e4", savedMoves[0].uci)
    }

    @Test
    fun finalizeGame_updates_result() = runBlocking {
        val gameId = "test-game-003"
        repository.createGame(gameId, System.currentTimeMillis())

        // Confirm initial result
        val initial = repository.loadGame(gameId)
        assertEquals("*", initial!!.first.result)

        repository.finalizeGame(gameId, "1-0")

        val updated = repository.loadGame(gameId)
        assertEquals("1-0", updated!!.first.result)
    }

    @Test
    fun saveMove_with_promotion_encodes_uci_correctly() = runBlocking {
        val gameId = "test-game-004"
        repository.createGame(gameId, System.currentTimeMillis())

        val promotionMove = ChessMove("e7", "e8", promotionPiece = PieceType.QUEEN, san = "e8=Q")
        repository.saveMove(gameId, 0, promotionMove, "some/fen")

        val loaded = repository.loadGame(gameId)!!.second
        assertEquals(1, loaded.size)
        assertEquals("e7e8q", loaded[0].uci)
    }
}
