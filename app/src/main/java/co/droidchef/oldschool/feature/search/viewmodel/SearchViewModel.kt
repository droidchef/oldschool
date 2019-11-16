package co.droidchef.oldschool.feature.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.droidchef.oldschool.util.SingleLiveEvent
import co.droidchef.oldschool.images.ImageLoader
import co.droidchef.oldschool.models.Photo
import co.droidchef.oldschool.network.Callback
import co.droidchef.oldschool.network.NetworkError
import co.droidchef.oldschool.network.NetworkRequestManager
import co.droidchef.oldschool.provider.RepositoryProvider
import java.util.*

class SearchViewModel : ViewModel() {

    private lateinit var searchQuery: String

    val isWorkInProgress: LiveData<Boolean>
        get() = _isWorkInProgress

    val photos: LiveData<ArrayList<Photo>>
        get() = _photos

    val errorMessage = SingleLiveEvent<String>()

    private var _photos = MutableLiveData<ArrayList<Photo>>()

    private var _isWorkInProgress = MutableLiveData<Boolean>()



    private val requestInProgressMap = HashMap<String, Int>()

    private val flickrPhotosRepository = RepositoryProvider.flickrPhotosRepository


    fun performSearchFor(query: String) {
        // start showing progress
        _isWorkInProgress.value = true

        if (query.isNotEmpty()) {
            searchQuery = query

            flickrPhotosRepository.getPhotos(searchQuery, object :
                Callback<ArrayList<Photo>> {
                override fun onSuccess(response: ArrayList<Photo>) {
                    _photos.postValue(response)
                    _isWorkInProgress.postValue(false)
                }

                override fun onFailure(networkError: NetworkError) {
                    handleNetworkError(networkError)
                    _isWorkInProgress.postValue(false)
                }

            })
        } else {
            _isWorkInProgress.postValue(false)
        }
    }

    fun loadMore() {

        val pageInProgress = requestInProgressMap[searchQuery]

        val lastFetchedPage = flickrPhotosRepository.getCurrentPageCountForQuery(searchQuery)

        val nextPageNumber = lastFetchedPage + 1

        // If the next page number and current page in progress is not different it means we are still
        // processing the page, so lets defer the call.
        if (pageInProgress != nextPageNumber) {
            requestInProgressMap[searchQuery] = nextPageNumber

            flickrPhotosRepository.getPhotosForPage(
                searchQuery,
                nextPageNumber,
                object : Callback<ArrayList<Photo>> {
                    override fun onSuccess(response: ArrayList<Photo>) {
                        requestInProgressMap.remove(searchQuery)
                        _photos.postValue(response)
                        _isWorkInProgress.postValue(false)
                    }

                    override fun onFailure(networkError: NetworkError) {
                        handleNetworkError(networkError)
                        requestInProgressMap.remove(searchQuery)
                        _isWorkInProgress.postValue(false)
                    }

                })

        }
    }

    private fun handleNetworkError(networkError: NetworkError) {

        when (networkError) {
            is NetworkError.YourPhoneScrewedUp -> {
                errorMessage.postValue("Please Check Your Internet Connection")
            }
            is NetworkError.OurClientScrewedUp -> {
                errorMessage.postValue("It looks like you're not authenticated!")
            }
            is NetworkError.OurServerScrewedUp -> {
                errorMessage.postValue("We're experiencing server issues, please try again in a while!")
            }
        }

    }

    override fun onCleared() {

        NetworkRequestManager.shutDown()

        ImageLoader.shutDown()

        super.onCleared()
    }
}
