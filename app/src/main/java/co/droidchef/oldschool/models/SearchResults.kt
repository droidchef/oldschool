package co.droidchef.oldschool.models

data class SearchResults(
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val total: String,
    val photos: ArrayList<Photo>
)