package co.droidchef.oldschool.network.service

import co.droidchef.oldschool.network.Callback
import co.droidchef.oldschool.models.PhotosResponse
import co.droidchef.oldschool.models.SearchResults
import co.droidchef.oldschool.network.NetworkError
import co.droidchef.oldschool.network.NetworkRequest
import co.droidchef.oldschool.network.NetworkRequestManager
import co.droidchef.oldschool.parser.JsonToEntityConverter
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL

object FlickrApiService {

    const val API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736"
    const val BASE_API_URL = "https://api.flickr.com"
    const val FLICKR_API_METHOD = "flickr.photos.search"


    fun searchPhotos(query: String, page: Int = 1, callback: Callback<SearchResults>) {
        val endpoint =
            URL(
                "$BASE_API_URL/services/rest/" +
                        "?method=$FLICKR_API_METHOD" +
                        "&api_key=$API_KEY" +
                        "&format=json" +
                        "&nojsoncallback=1" +
                        "&safe_search=1" +
                        "&text=$query" +
                        "&page=$page" +
                        "&extras=url_m"
            )

        val request = NetworkRequest(
            endpoint,
            object : NetworkRequest.Callback {
                override fun onSuccess(inputStream: InputStream) {
                    try {
                        val jsonObject =
                            getJsonFrom(
                                inputStream
                            )

                        println(jsonObject.toString(2))

                        val photosResponse = JsonToEntityConverter.convert(
                            jsonObject,
                            PhotosResponse::class.java
                        ) as PhotosResponse

                        callback.onSuccess(photosResponse.photos)

                    } catch (exception: Exception) {
                        callback.onFailure(NetworkError.OurClientScrewedUp("Couldn't parse the JSON Object"))
                    }
                }

                override fun onFailure(errorCode: Int) {

                    when (errorCode) {
                        -1 -> {
                            callback.onFailure(NetworkError.YourPhoneScrewedUp("No Internet"))
                        }
                        4 -> {
                            callback.onFailure(NetworkError.OurClientScrewedUp("Are you authenticated?"))
                        }

                        5 -> {
                            callback.onFailure(NetworkError.OurServerScrewedUp("We are experiencing issues with our servers right now."))
                        }

                    }
                }

            })

        NetworkRequestManager.processRequest(request)
    }


    fun getJsonFrom(inputStream: InputStream): JSONObject {

        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        val sb = StringBuilder()
        var line = bufferedReader.readLine()
        while (line != null) {
            sb.append(line)
            sb.append("\n")
            line = bufferedReader.readLine()
        }

        inputStream.close()

        return JSONObject(sb.toString())
    }
}