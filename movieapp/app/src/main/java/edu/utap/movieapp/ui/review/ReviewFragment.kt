package edu.utap.movieapp.ui.review

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.movieapp.MainViewModel
import edu.utap.movieapp.R
import edu.utap.movieapp.databinding.FragmentReviewBinding


class ReviewFragment :
    Fragment(R.layout.fragment_review) {
    private val viewModel: MainViewModel by activityViewModels()

    // https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder#getBindingAdapterPosition()
    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }
    private fun initTouchHelper(): ItemTouchHelper {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START)
            {
                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    val position = getPos(viewHolder)
                    Log.d(javaClass.simpleName, "Swipe delete $position")
                    viewModel.removeReviewAt(position)
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentReviewBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated")
        // Create new review
        // Don't need action, because sending default argument
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_review_to_navigation_review_edit)
        }

        // Long press to edit.
        val adapter = ReviewsAdapter(viewModel) {position ->
            val action =
                ReviewFragmentDirections.actionNavigationReviewToNavigationReviewEdit(position, "Edit Review")
            findNavController().navigate(action)
        }

        val rv = binding.reviewListRV
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        binding.reviewListRV.addItemDecoration(itemDecor)
        binding.reviewListRV.adapter = adapter
        // Swipe left to delete
        initTouchHelper().attachToRecyclerView(rv)

        viewModel.observeReviews().observe(viewLifecycleOwner) {
            Log.d(javaClass.simpleName, "reviewList observe len ${it.size}")

            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.observeReviewsEmpty().observe(viewLifecycleOwner) {
            if(it) {
                binding.emptyNotesView.visibility = View.VISIBLE
            } else {
                binding.emptyNotesView.visibility = View.GONE
            }
        }
    }
}