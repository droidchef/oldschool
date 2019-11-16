package co.droidchef.oldschool

import co.droidchef.oldschool.models.Photo
import co.droidchef.oldschool.models.PhotosResponse
import co.droidchef.oldschool.models.SearchResults
import co.droidchef.oldschool.parser.JsonToEntityConverter
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JsonToEntityConverterTest {

    @Test
    fun `test if photo is parsed correctly`() {
        val jsonString = "{\n" +
                " \"id\": \"49101590698\",\n" +
                " \"owner\": \"38941615@N02\",\n" +
                " \"secret\": \"6f5a0732a7\",\n" +
                " \"server\": \"65535\",\n" +
                " \"farm\": 66,\n" +
                " \"title\": \"Zarpazos ♥\",\n" +
                " \"ispublic\": 1,\n" +
                " \"isfriend\": 0,\n" +
                " \"isfamily\": 0,\n" +
                " \"url_m\": \"https:\\/\\/live.staticflickr.com\\/65535\\/49101590698_6f5a0732a7.jpg\",\n" +
                " \"height_m\": 500,\n" +
                " \"width_m\": 438\n" +
                "}"
        val expectedPhoto = Photo(
            "49101590698",
            "6f5a0732a7",
            "65535",
            66,
            "https://live.staticflickr.com/65535/49101590698_6f5a0732a7.jpg",
            438,
            500
        )
        val actualPhoto = JsonToEntityConverter.convert(JSONObject(jsonString), Photo::class.java)
        assertEquals(expectedPhoto, actualPhoto)
    }

    @Test
    fun `test if photos response is parsed correctly`() {

        val jsonString = "{\n" +
                "  \"photos\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pages\": 2044,\n" +
                "    \"perpage\": 2,\n" +
                "    \"total\": \"204393\",\n" +
                "    \"photo\": [\n" +
                "      {\n" +
                "        \"id\": \"49101882447\",\n" +
                "        \"owner\": \"61056422@N07\",\n" +
                "        \"secret\": \"797d22dbfa\",\n" +
                "        \"server\": \"65535\",\n" +
                "        \"farm\": 66,\n" +
                "        \"title\": \"DSCF0705.jpg\",\n" +
                "        \"ispublic\": 1,\n" +
                "        \"isfriend\": 0,\n" +
                "        \"isfamily\": 0,\n" +
                "        \"url_m\": \"https:\\/\\/live.staticflickr.com\\/65535\\/49101882447_797d22dbfa.jpg\",\n" +
                "        \"height_m\": 281,\n" +
                "        \"width_m\": 500\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"49101880132\",\n" +
                "        \"owner\": \"61056422@N07\",\n" +
                "        \"secret\": \"7e1d0f4164\",\n" +
                "        \"server\": \"65535\",\n" +
                "        \"farm\": 66,\n" +
                "        \"title\": \"DSCF0702.jpg\",\n" +
                "        \"ispublic\": 1,\n" +
                "        \"isfriend\": 0,\n" +
                "        \"isfamily\": 0,\n" +
                "        \"url_m\": \"https:\\/\\/live.staticflickr.com\\/65535\\/49101880132_7e1d0f4164.jpg\",\n" +
                "        \"height_m\": 281,\n" +
                "        \"width_m\": 500\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"stat\": \"ok\"\n" +
                "}"
        val expectedPhotosResponse = PhotosResponse(
            SearchResults(
                1, 2044, 2, "204393", arrayListOf(
                    Photo(
                        "49101882447",
                        "797d22dbfa",
                        "65535",
                        66,
                        "https://live.staticflickr.com/65535/49101882447_797d22dbfa.jpg",
                        500,
                        281
                    ),
                    Photo(
                        "49101880132",
                        "7e1d0f4164",
                        "65535",
                        66,
                        "https://live.staticflickr.com/65535/49101880132_7e1d0f4164.jpg",
                        500,
                        281
                    )
                )
            ), "ok"
        )

        val actualPhotosResponse =
            JsonToEntityConverter.convert(JSONObject(jsonString), PhotosResponse::class.java)

        assertEquals(expectedPhotosResponse, actualPhotosResponse)

    }


    @Test
    fun `test if search results are parsed correctly`() {

        val jsonString = "{\n" +
                "    \"page\": 1,\n" +
                "    \"pages\": 2044,\n" +
                "    \"perpage\": 2,\n" +
                "    \"total\": \"204393\",\n" +
                "    \"photo\": [\n" +
                "      {\n" +
                "        \"id\": \"49101882447\",\n" +
                "        \"owner\": \"61056422@N07\",\n" +
                "        \"secret\": \"797d22dbfa\",\n" +
                "        \"server\": \"65535\",\n" +
                "        \"farm\": 66,\n" +
                "        \"title\": \"DSCF0705.jpg\",\n" +
                "        \"ispublic\": 1,\n" +
                "        \"isfriend\": 0,\n" +
                "        \"isfamily\": 0,\n" +
                "        \"url_m\": \"https:\\/\\/live.staticflickr.com\\/65535\\/49101882447_797d22dbfa.jpg\",\n" +
                "        \"height_m\": 281,\n" +
                "        \"width_m\": 500\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"49101880132\",\n" +
                "        \"owner\": \"61056422@N07\",\n" +
                "        \"secret\": \"7e1d0f4164\",\n" +
                "        \"server\": \"65535\",\n" +
                "        \"farm\": 66,\n" +
                "        \"title\": \"DSCF0702.jpg\",\n" +
                "        \"ispublic\": 1,\n" +
                "        \"isfriend\": 0,\n" +
                "        \"isfamily\": 0,\n" +
                "        \"url_m\": \"https:\\/\\/live.staticflickr.com\\/65535\\/49101880132_7e1d0f4164.jpg\",\n" +
                "        \"height_m\": 281,\n" +
                "        \"width_m\": 500\n" +
                "      }\n" +
                "    ]\n" +
                "  }"
        val expectedSearchResponse = SearchResults(
            1, 2044, 2, "204393", arrayListOf(
                Photo(
                    "49101882447",
                    "797d22dbfa",
                    "65535",
                    66,
                    "https://live.staticflickr.com/65535/49101882447_797d22dbfa.jpg",
                    500,
                    281
                ),
                Photo(
                    "49101880132",
                    "7e1d0f4164",
                    "65535",
                    66,
                    "https://live.staticflickr.com/65535/49101880132_7e1d0f4164.jpg",
                    500,
                    281
                )
            )
        )

        val actualSearchResponse =
            JsonToEntityConverter.convert(JSONObject(jsonString), SearchResults::class.java)

        assertEquals(expectedSearchResponse, actualSearchResponse)

    }

}