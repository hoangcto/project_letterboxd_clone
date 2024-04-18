package edu.utap.movie_app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.utap.movie_app.MainViewModel
import edu.utap.movie_app.databinding.FragmentHomeBinding


class HomeFragment: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Set up the adapter and recycler view
    private fun initAdapter(binding: FragmentHomeBinding) {
        val postRowAdapter = PostRowAdapter(viewModel) {
            Log.d("Movie",
                String.format("OnePost title %s",
                    if (it.title.length > 32)
                        it.title.substring(0, 31) + "..."
                    else it.title))
            Log.d("doOneMovie", "image ${it.posterPath}")
            // XXX Write me
            // Navigate to OnePostFragment and pass the selected RedditPost
//            val action = HomeFragmentDirections.actionHomeFragmentToOnePostFragment(it)
//            findNavController().navigate(action)
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
        viewModel.fetchPopularMovies()
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        // XXX Write me
        swipe.setOnRefreshListener {
            viewModel.repoFetch()
            swipe.isRefreshing = false
        }
    }

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

        // Get the selected subreddit from the navigation arguments
        initAdapter(binding)
        // Initialize the SwipeRefreshLayout
    }
}
//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}