package co.droidchef.oldschool.models

data class Photo(
    val id: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val imageUrl: String,
    val width: Int,
    val height: Int
)

fun Photo.getInterpolatedUrl() : String {
    return "http://farm${farm}.static.flickr.com/$server/${id}_${secret}.jpg"
}
