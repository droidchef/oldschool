package co.droidchef.oldschool.repository

import co.droidchef.oldschool.images.LruCache
import co.droidchef.oldschool.models.Photo
import co.droidchef.oldschool.models.SearchResults
import co.droidchef.oldschool.network.Callback
import co.droidchef.oldschool.network.NetworkError
import co.droidchef.oldschool.network.service.FlickrApiService

class FlickrPhotosRepositoryImpl(
    private val flickrApiService: FlickrApiService,
    private val photosResponseCache: LruCache<String, ArrayList<Photo>?>
) : FlickrPhotosRepository {

    private val pagesPerQueryCount = HashMap<String, Int>()

    override fun getPhotos(query: String, responseCallback: Callback<ArrayList<Photo>>) {

        photosResponseCache.get(query)?.apply {
            responseCallback.onSuccess(this)
        } ?: run {

            flickrApiService.searchPhotos(query, 1, callback = object :
                Callback<SearchResults> {
                override fun onSuccess(response: SearchResults) {
                    photosResponseCache.put(query, response.photos)
                    pagesPerQueryCount[query] = response.page
                    responseCallback.onSuccess(response.photos)
                }

                override fun onFailure(networkError: NetworkError) {
                    responseCallback.onFailure(networkError)
                }
            })
        }

    }

    override fun getPhotosForPage(
        query: String,
        page: Int,
        responseCallback: Callback<ArrayList<Photo>>
    ) {

        flickrApiService.searchPhotos(query, page, object :
            Callback<SearchResults> {
            override fun onSuccess(response: SearchResults) {
                photosResponseCache.get(query)?.apply {
                    this.addAll(response.photos)
                } ?: run {
                    photosResponseCache.put(query, response.photos)
                }

                pagesPerQueryCount[query] = response.page

                responseCallback.onSuccess(response.photos)
            }

            override fun onFailure(networkError: NetworkError) {
                responseCallback.onFailure(networkError)
            }
        })
    }

    override fun getCurrentPageCountForQuery(query: String): Int {
        pagesPerQueryCount[query]?.let {
            return it
        } ?: run {
            return 0
        }
    }


}