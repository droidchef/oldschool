package co.droidchef.oldschool

import co.droidchef.oldschool.images.LruCache
import co.droidchef.oldschool.models.Photo
import co.droidchef.oldschool.network.Callback
import co.droidchef.oldschool.network.service.FlickrApiService
import co.droidchef.oldschool.repository.FlickrPhotosRepository
import co.droidchef.oldschool.repository.FlickrPhotosRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Before
import org.junit.Test


class FlickrPhotosRepositoryTest {

    private lateinit var flickrPhotosRepository: FlickrPhotosRepository

    private val flickrService = mockk<FlickrApiService>(relaxed = true)

    private val cbPhotos = mockk<Callback<ArrayList<Photo>>>(relaxed = true)

    private val lruCache = mockk<LruCache<String, ArrayList<Photo>?>>()

    @Before
    fun setup() {
        flickrPhotosRepository = FlickrPhotosRepositoryImpl(flickrService, lruCache)
    }

    @Test
    fun `test if cache is checked for each search`() {

        val searchQuery = "cats"

        every { lruCache.get(searchQuery) } returns null

        flickrPhotosRepository.getPhotos(searchQuery, cbPhotos)

        verify(exactly = 1) { lruCache.get(searchQuery) }

        verify(exactly = 1) { flickrService.searchPhotos(searchQuery, 1, any()) }

    }

    @Test
    fun `test if the order of data access is memory and then network`() {
        val searchQuery = "monkey"

        every { lruCache.get(searchQuery) } returns null

        flickrPhotosRepository.getPhotos(searchQuery, cbPhotos)

        verifyOrder {
            lruCache.get(searchQuery)
            flickrService.searchPhotos(searchQuery, 1, any())
        }

    }

    @Test
    fun `verify that a network request is not made if cache has data for the query`() {

        val searchQuery = "rats"

        every { lruCache.get(searchQuery) } returns arrayListOf()

        flickrPhotosRepository.getPhotos(searchQuery, cbPhotos)

        verify(exactly = 1) { lruCache.get(searchQuery) }

        verify(exactly = 0) { flickrService.searchPhotos(searchQuery, 1, any()) }

    }

}