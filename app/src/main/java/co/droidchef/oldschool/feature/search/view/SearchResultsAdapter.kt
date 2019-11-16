package co.droidchef.oldschool.feature.search.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.droidchef.oldschool.R
import co.droidchef.oldschool.images.ImageLoader
import co.droidchef.oldschool.models.Photo
import co.droidchef.oldschool.models.getInterpolatedUrl


class SearchResultsAdapter(private val photos: ArrayList<Photo>) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.photo_grid_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val photoImageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(photo: Photo) {
            photo.imageUrl.takeIf { it.isNotEmpty() }
                ?.apply {
                    ImageLoader.load(
                        this,
                        photoImageView,
                        photo.width,
                        photo.height,
                        R.drawable.placeholder
                    )
                }
                ?: run {
                    ImageLoader.load(
                        photo.getInterpolatedUrl(),
                        photoImageView,
                        R.drawable.placeholder
                    )
                }
        }

    }

    override fun getItemId(position: Int): Long {
        return photos[position].id.hashCode().toLong()
    }

    fun addPhotos(newPhotos: ArrayList<Photo>) {
        photos.addAll(newPhotos)
        notifyDataSetChanged()
    }

    fun clear() {
        photos.clear()
        notifyDataSetChanged()
    }

}

