package co.droidchef.oldschool.network

import android.os.Process
import androidx.annotation.WorkerThread
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class NetworkRequest(
    private val url: URL,
    private val callback: Callback
) : Runnable {

    interface Callback {
        fun onSuccess(inputStream: InputStream)

        fun onFailure(errorCode: Int)
    }

    @WorkerThread
    private fun establishConnectionForReadingBytes(url: URL): HttpURLConnection {
        val httpConnection = url.openConnection() as HttpURLConnection
        httpConnection.requestMethod = "GET"
        httpConnection.connectTimeout = 1000
        httpConnection.readTimeout = 5000
        httpConnection.instanceFollowRedirects = false
        httpConnection.useCaches = false
        httpConnection.connect()
        return httpConnection
    }

    @WorkerThread
    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
        try {

            val connection = establishConnectionForReadingBytes(url)

            val responseCode = connection.responseCode

            when (responseCode / 100) {

                HTTP_CODE_SUCCESS -> {
                    callback.onSuccess(connection.inputStream)
                }
                else -> {
                    callback.onFailure(responseCode)
                }

            }

        } catch (exception: Exception) {
            callback.onFailure(-1)
        }

    }


    companion object {
        const val HTTP_CODE_SUCCESS = 2
    }


}
