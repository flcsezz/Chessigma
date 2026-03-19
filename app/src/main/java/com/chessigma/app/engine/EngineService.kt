package com.chessigma.app.engine

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EngineService : Service() {

    @Inject lateinit var stockfishEngine: StockfishEngine
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): EngineService = this@EngineService
    }

    override fun onBind(intent: Intent): IBinder {
        // Return binder to the service so ViewModels/Repository can access it
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Start foreground service with notification
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stockfishEngine.shutdown()
    }
}
