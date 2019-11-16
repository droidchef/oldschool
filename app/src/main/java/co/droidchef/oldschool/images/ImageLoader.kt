package co.droidchef.oldschool.images

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.collection.LruCache
import androidx.constraintlayout.widget.Placeholder
import co.droidchef.oldschool.R
import co.droidchef.oldschool.network.NetworkRequestManager
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object ImageLoader {

    private val executorService = Executors.newFixedThreadPool(8)

    /**
     * Using 1/2 of the Maximum Available Memory for now, as there is nothing else
     * the app needs it for. We are only displaying images.
     * However, when more screens are added, this value must be reconsidered to free up space
     * for other things in the app.
     */
    private val IN_MEMORY_LRU_CACHE_SIZE_IN_KB =
        (Runtime.getRuntime().maxMemory() / 1024).toInt() / 2


    private val lruCacheInMemory =
        object : LruCache<String, Bitmap>(IN_MEMORY_LRU_CACHE_SIZE_IN_KB) {

            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }


    fun load(url: String, into: ImageView, @DrawableRes placeholder: Int) {

        load(url, into, null, null, placeholder)
    }

    fun load(url: String, into: ImageView, width: Int?, height: Int?, @DrawableRes placeholder: Int) {

        lruCacheInMemory.get(url)?.also {
            into.setImageBitmap(it)
        } ?: run {
            executorService.submit(
                ImageLoadingRequest(
                    url,
                    WeakReference(into),
                    width,
                    height,
                    placeholder
                )
            )
        }
    }

    /**
     * Already added support for passing possible max (width and height) of the image view so that
     * if I need to downscale/sample images I can work with these numbers.
     */
    private class ImageLoadingRequest(
        private val urlToLoadImageFrom: String,
        private val imageView: WeakReference<ImageView>,
        private val width: Int? = null,
        private val height: Int? = null,
        @DrawableRes private val placeholder: Int
    ) : Runnable {
        private lateinit var httpURLConnection: HttpURLConnection

        private lateinit var inputStream: InputStream

        private val handler by lazy { Handler(Looper.getMainLooper()) }

        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            try {
                val inputStream = loadData(URL(urlToLoadImageFrom), null)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                lruCacheInMemory.put(urlToLoadImageFrom, bitmap)

                handler.post {
                    imageView.get()?.setImageBitmap(bitmap)
                }

            } catch (exception: Exception) {
                handler.post {
                    imageView.get()?.setImageResource(placeholder)
                }
            } finally {
                cleanup()
            }
        }

        private fun loadData(url: URL, lastURL: URL?): InputStream {

            establishConnectionForReadingBytes(url)

            inputStream = httpURLConnection.inputStream

            val statusCode = httpURLConnection.responseCode

            when (statusCode / 100) {

                HTTP_CODE_SUCCESS -> {
                    return httpURLConnection.inputStream
                }
                HTTP_CODE_REDIRECT -> {
                    val redirectUrlString = httpURLConnection.getHeaderField("Location")
                    val redirectURL = URL(url, redirectUrlString)
                    cleanup()

                    if (lastURL == redirectURL) {
                        throw RuntimeException("We are in a redirection loop, let's commit suicide.")
                    }

                    return loadData(redirectURL, lastURL)
                }
                else -> {
                    cleanup()
                    // TODO: Handle Errors here
                    throw RuntimeException("Error processing the request")
                }
            }

        }

        private fun establishConnectionForReadingBytes(url: URL) {
            httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connectTimeout = 5000
            httpURLConnection.readTimeout = 5000
            httpURLConnection.useCaches = false
            httpURLConnection.doInput = true
            httpURLConnection.instanceFollowRedirects = false
            httpURLConnection.connect()
        }

        private fun cleanup() {
            inputStream.close()
            httpURLConnection.disconnect()
        }

        companion object {
            const val HTTP_CODE_SUCCESS = 2
            const val HTTP_CODE_REDIRECT = 3
        }
    }

    fun shutDown() {
        Log.d("ImageLoader", "is going to shut down now...")
        if (!executorService.isShutdown) {
            executorService.shutdownNow()
        }
    }
}

