package co.droidchef.oldschool.network

import android.util.Log
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object NetworkRequestManager {

    private val executorService: ExecutorService = Executors.newFixedThreadPool(12)

    fun processRequest(networkRequest: NetworkRequest) {
        executorService.submit(networkRequest)
    }

    fun shutDown() {
        Log.d("NetworkRequestManager", "is going to shut down now...")
        if (!executorService.isShutdown) {
            executorService.shutdownNow()
        }
    }

}