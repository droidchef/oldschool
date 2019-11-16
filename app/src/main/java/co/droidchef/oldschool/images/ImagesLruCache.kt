package co.droidchef.oldschool.images

import co.droidchef.oldschool.models.Photo

class ImagesLruCache : LruCache<String, ArrayList<Photo>?> {

    private val photosResponseCache = android.util.LruCache<String, ArrayList<Photo>>(4)

    override fun get(k: String): ArrayList<Photo>? {
        photosResponseCache[k]?.let {
            return it
        }?: kotlin.run {
            return null
        }
    }

    override fun put(k: String, v: ArrayList<Photo>?) {
        photosResponseCache.put(k, v)
    }

}