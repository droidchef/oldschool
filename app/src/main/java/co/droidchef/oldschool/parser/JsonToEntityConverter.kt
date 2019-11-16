package co.droidchef.oldschool.parser

import co.droidchef.oldschool.models.Photo
import co.droidchef.oldschool.models.PhotosResponse
import co.droidchef.oldschool.models.SearchResults
import org.json.JSONObject

object JsonToEntityConverter {

    fun convert(from: JSONObject, to: Class<out Any>): Any {
        return when (to) {
            PhotosResponse::class.java -> {
                return parsePhotoResponse(
                    jsonObject = from
                )
            }
            SearchResults::class.java -> {
                return parseSearchResults(
                    jsonObject = from
                )
            }
            Photo::class.java -> {
                return parsePhoto(
                    jsonObject = from
                )
            }
            else -> {

            }
        }

    }

    private fun parseSearchResults(jsonObject: JSONObject): SearchResults {

        val photos = arrayListOf<Photo>()

        jsonObject.optJSONArray("photo")?.let {
            for (i in 0 until it.length()) {
                photos.add(
                    parsePhoto(
                        it.getJSONObject(i)
                    )
                )
            }
            return@let photos
        }

        return SearchResults(
            jsonObject.optInt("page"),
            jsonObject.optInt("pages"),
            jsonObject.optInt("perpage"),
            jsonObject.optString("total"),
            photos
        )

    }

    private fun parsePhoto(jsonObject: JSONObject): Photo {
        return Photo(
            jsonObject.optString("id"),
            jsonObject.optString("secret"),
            jsonObject.optString("server"),
            jsonObject.optInt("farm"),
            jsonObject.optString("url_m"),
            jsonObject.optInt("width_m"),
            jsonObject.optInt("height_m")
        )
    }

    private fun parsePhotoResponse(jsonObject: JSONObject): PhotosResponse {
        return PhotosResponse(
            parseSearchResults(
                jsonObject.getJSONObject("photos")
            ),
            jsonObject.optString("stat")
        )
    }
}