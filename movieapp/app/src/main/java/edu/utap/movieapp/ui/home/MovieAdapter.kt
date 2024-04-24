package edu.utap.movieapp.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.movieapp.MainViewModel
import edu.utap.movieapp.api.MovieResponse
import edu.utap.movieapp.databinding.RowPostBinding
import edu.utap.movieapp.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

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
        val rowHeight = (screenHeight * 0.22).toInt()

        // Set the height of the root LinearLayout
        rowBinding.root.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            rowHeight
        )

        rowBinding.apply {

            movieTitle.text = item.title
            releaseDate.text = convertReleaseDate(item.releaseDate)
            popularityScore.text = "Popularity Score: " + item.popularity.toInt().toString()
            voteAverage.text = "User Score: " + String.format("%.1f", item.voteAverage)

            //load image
            Glide.glideFetch(item.posterPath, rowImageView)
            movieTitle.setOnClickListener { navigateToOnePost(item) }
            popularityScore.setOnClickListener { navigateToOnePost(item) }
            rowImageView.setOnClickListener { navigateToOnePost(item) }
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

    fun convertReleaseDate(releaseDateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        try {
            val date = inputFormat.parse(releaseDateString)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            return releaseDateString // Return the original string if parsing fails
        }
    }
}
