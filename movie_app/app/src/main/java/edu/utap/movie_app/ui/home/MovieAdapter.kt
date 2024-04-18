package edu.utap.movie_app.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.movie_app.MainViewModel
import edu.utap.movie_app.api.MovieResponse
import edu.utap.movie_app.databinding.RowPostBinding
import edu.utap.movie_app.glide.Glide

class PostRowAdapter(private val viewModel: MainViewModel,
                     private val navigateToOnePost: (MovieResponse)->Unit )
    : ListAdapter<MovieResponse, PostRowAdapter.VH>(MovieDiff()) {
    private fun getPos(holder: VH) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }


    inner class VH(val rowPostBinding: RowPostBinding) : RecyclerView.ViewHolder(rowPostBinding.root) {

//        init {
//            // Only set onclick listener when we create the holder
//            rowPostBinding.rowFav.setOnClickListener {
//                val position = bindingAdapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    val post = getItem(position)
//                    //val isFavorite = viewModel.observeFavorites().value?.contains(post) == true
//                    val isFavorite = post.isFavorite
//                    if (isFavorite) {
//                        viewModel.removeFavorite(post)
//                    } else {
//                        viewModel.addFavorite(post)
//                    }
//                    post.isFavorite = !isFavorite
//                    updateFavIconImage(rowPostBinding.rowFav, !isFavorite)
//                    Log.d(javaClass.simpleName, "favorites posts for subreddit: ${viewModel.observeFavorites().value}")
//                }
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        //XXX Write me.
        val rowPostBinding = RowPostBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowPostBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        //XXX Write me.
        val rowBinding = holder.rowPostBinding
        val item = getItem(position)
        val displayMetrics = holder.itemView.context.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val rowHeight = (screenHeight * 0.25).toInt()

        // Set the height of the root LinearLayout
        rowBinding.root.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            rowHeight
        )

        rowBinding.apply {

            rowBinding.movieTitle.text = item.title
            rowBinding.popularityScore.text = "Popularity Score: " + item.popularity.toInt().toString()
            rowBinding.voteAverage.text = "User Score: " + String.format("%.1f", item.voteAverage)

            //load image
            item.posterPath?.let { Glide.glideFetch(it, item.posterPath, rowBinding.rowImageView) }
            rowBinding.movieTitle.setOnClickListener { navigateToOnePost(item) }
            rowBinding.popularityScore.setOnClickListener { navigateToOnePost(item) }
            rowBinding.rowImageView.setOnClickListener { navigateToOnePost(item) }

//            if(viewModel.observeFavorites().value?.contains(item) == true){
//                item.isFavorite = true
//            }
//            updateFavIconImage(rowBinding.rowFav, item.isFavorite)
            // Set the initial state of the favorite icon

        }
    }

    class MovieDiff : DiffUtil.ItemCallback<MovieResponse>() {
        override fun areItemsTheSame(oldItem: MovieResponse, newItem: MovieResponse): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: MovieResponse, newItem: MovieResponse): Boolean {
            return MovieResponse.stringsEqual(oldItem.title, newItem.title) &&
                    MovieResponse.stringsEqual(oldItem.releaseDate, newItem.releaseDate)
        }
    }

//    private fun updateFavIconImage(imageView: ImageView, isFavorite: Boolean) {
//        imageView.setImageResource(
//            if (isFavorite) R.drawable.ic_favorite_black_24dp // Replace with your filled heart icon
//            else R.drawable.ic_favorite_border_black_24dp // Replace with your empty heart icon
//        )
//    }
}