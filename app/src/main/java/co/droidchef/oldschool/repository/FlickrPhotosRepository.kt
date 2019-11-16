package co.droidchef.oldschool.repository

import co.droidchef.oldschool.network.Callback
import co.droidchef.oldschool.models.Photo

interface FlickrPhotosRepository {

    fun getPhotos(query: String, responseCallback: Callback<ArrayList<Photo>>)

    fun getPhotosForPage(query: String, page: Int, responseCallback: Callback<ArrayList<Photo>>)

    fun getCurrentPageCountForQuery(query: String) : Int

}