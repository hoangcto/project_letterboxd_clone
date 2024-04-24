package edu.utap.movieapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.movieapp.MainViewModel
import edu.utap.movieapp.R
import edu.utap.movieapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var selectedButton: Button? = null

    // Set up the adapter and recycler view
    private fun initAdapter(binding: FragmentHomeBinding) {
        val postRowAdapter = PostRowAdapter(viewModel) {
            // Navigate to OnePostFragment and pass the selected Movie
            val action = HomeFragmentDirections.actionHomeFragmentToOnePostFragment(it, it.title)
            findNavController().navigate(action)
        }

        val rv = binding.recyclerView
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postRowAdapter
        }

        viewModel.observeMovies().observe(viewLifecycleOwner) { movies ->
            postRowAdapter.submitList(movies)
            postRowAdapter.notifyDataSetChanged()
        }

//        viewModel.fetchPopularMovies()
    }

//    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
//        // XXX Write me
//        swipe.setOnRefreshListener {
//            viewModel.repoFetch()
//            swipe.isRefreshing = false
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPopular = binding.btnPopular
        val btnNowPlaying = binding.btnNowPlaying
        val btnTopRated = binding.btnTopRated

        val defaultBackgroundColor = ContextCompat.getColor(requireContext(), R.color.button_background_tint)
        val defaultTextColor = ContextCompat.getColor(requireContext(), R.color.button_text_color)
        val highlightedBackgroundColor = ContextCompat.getColor(requireContext(), R.color.highlighted_button_background_tint)
        val highlightedTextColor = ContextCompat.getColor(requireContext(), R.color.highlighted_button_text_color)

        if (viewModel.currentButton == 0) {
            updateButtonHighlight(btnPopular, btnNowPlaying, btnTopRated, highlightedBackgroundColor, defaultBackgroundColor, highlightedTextColor, defaultTextColor)
        } else if (viewModel.currentButton == 1) {
            updateButtonHighlight(btnTopRated, btnNowPlaying, btnPopular, highlightedBackgroundColor, defaultBackgroundColor, highlightedTextColor, defaultTextColor)
        } else {
            updateButtonHighlight(btnNowPlaying, btnPopular, btnTopRated, highlightedBackgroundColor, defaultBackgroundColor, highlightedTextColor, defaultTextColor)
        }

        // Get the selected subreddit from the navigation arguments
        initAdapter(binding)

        binding.btnPopular.setOnClickListener{
            updateButtonHighlight(btnPopular, btnNowPlaying, btnTopRated, highlightedBackgroundColor, defaultBackgroundColor, highlightedTextColor, defaultTextColor)
            viewModel.fetchPopularMovies()
            viewModel.currentButton = 0
        }
        binding.btnTopRated.setOnClickListener{
            updateButtonHighlight(btnTopRated, btnNowPlaying, btnPopular, highlightedBackgroundColor, defaultBackgroundColor, highlightedTextColor, defaultTextColor)
            viewModel.fetchTopMovies()
            viewModel.currentButton = 1
        }
        binding.btnNowPlaying.setOnClickListener{
            updateButtonHighlight(btnNowPlaying, btnPopular, btnTopRated, highlightedBackgroundColor, defaultBackgroundColor, highlightedTextColor, defaultTextColor)
            viewModel.fetchPlayingMovies()
            viewModel.currentButton = 2
        }
    }
    private fun updateButtonHighlight(selectedButton: Button, otherButton: Button, otherButton2: Button, highlightedBackgroundColor: Int, defaultBackgroundColor: Int, highlightedTextColor: Int, defaultTextColor: Int) {
        otherButton.setBackgroundColor(defaultBackgroundColor)
        otherButton.setTextColor(defaultTextColor)
        otherButton2.setBackgroundColor(defaultBackgroundColor)
        otherButton2.setTextColor(defaultTextColor)
        selectedButton.setBackgroundColor(highlightedBackgroundColor)
        selectedButton.setTextColor(highlightedTextColor)
        this.selectedButton = selectedButton
    }

}