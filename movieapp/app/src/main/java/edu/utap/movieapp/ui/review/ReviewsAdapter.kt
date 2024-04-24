package edu.utap.movieapp.ui.review

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import edu.utap.movieapp.MainViewModel
import edu.utap.movieapp.databinding.ReviewListRowBinding
import edu.utap.movieapp.glide.Glide
import edu.utap.movieapp.model.Review
import java.util.*


class ReviewsAdapter(private val viewModel: MainViewModel,
                     private val editReview: (Int)->Unit)
    : ListAdapter<Review, ReviewsAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.text == newItem.text
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.poster_path == newItem.poster_path
                    && oldItem.timestamp == newItem.timestamp
        }
    }

    // Puts the time first, which is most important.  But date is useful too
    private val dateFormat: DateFormat =
        SimpleDateFormat("hh:mm MMM dd, yyyy", Locale.US)

    // https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder#getBindingAdapterPosition()
    // Getting the position of the selected item is unfortunately complicated
    // This always returns a valid index.
    private fun getPos(holder: VH) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }

    inner class VH(val reviewListRowBinding: ReviewListRowBinding) :
        RecyclerView.ViewHolder(reviewListRowBinding.root) {

        private fun bindPic1(posterPath: String) {
            if(posterPath != "") {
                Glide.glideFetch(posterPath, reviewListRowBinding.reviewlistPoster)
            } else {
                reviewListRowBinding.reviewlistPoster.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }

        init {
            reviewListRowBinding.reviewRowRV.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        fun bind(holder: VH, position: Int) {
            val review = viewModel.getReview(position)

            review.timestamp?.let {
                holder.reviewListRowBinding.timestamp.text = "${dateFormat.format(it.toDate())} by ${review.name}"
            }
            holder.reviewListRowBinding.reviewTitle.text =   "${review.movieTitle} (${review.releaseDate.take(4)})"
            holder.reviewListRowBinding.text.text = review.text
            bindPic1(review.poster_path)

            // check if the current user's ID matches the review owner's ID
            val currentUserId = viewModel.getCurrentUserUid()
            if (currentUserId == review.ownerUid) {
                // Show the edit button if the user is the owner of the review
                holder.reviewListRowBinding.editButton.visibility = View.VISIBLE
                holder.reviewListRowBinding.editButton.setOnClickListener {
                    editReview(getPos(this))
                    Log.d(javaClass.simpleName, "editnote being click on")
                }
            } else {
                // Hide the edit button if the user is not the owner of the review
                holder.reviewListRowBinding.editButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val reviewListRowBinding = ReviewListRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(reviewListRowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)

    }
}