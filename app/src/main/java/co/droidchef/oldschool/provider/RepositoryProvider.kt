package co.droidchef.oldschool.provider

import co.droidchef.oldschool.images.ImagesLruCache
import co.droidchef.oldschool.network.service.FlickrApiService
import co.droidchef.oldschool.repository.FlickrPhotosRepository
import co.droidchef.oldschool.repository.FlickrPhotosRepositoryImpl

object RepositoryProvider {

    val flickrPhotosRepository : FlickrPhotosRepository by lazy {
        return@lazy FlickrPhotosRepositoryImpl(FlickrApiService, ImagesLruCache())
    }

}