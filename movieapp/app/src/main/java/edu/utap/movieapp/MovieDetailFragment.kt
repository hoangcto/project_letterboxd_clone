package edu.utap.movieapp;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import edu.utap.movieapp.databinding.FragmentOnePostBinding
import edu.utap.movieapp.glide.Glide

class MovieDetailFragment : Fragment() {
    private var _binding: FragmentOnePostBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val args: MovieDetailFragmentArgs by navArgs()
    private var runtime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable the options menu (for the toolbar)
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movie = args.movie
        val actionBarTitle = args.movieTitle
        viewModel.fetchMovieDetails(movie.id)
        viewModel.observeMovieDetails().observe(viewLifecycleOwner) {details ->
            if (details!!.tagline != "") {
                binding.overview.text = details.tagline
                runtime = details.runtime!!
                binding.runtime.text = "Runtime: ${runtime / 60}h ${runtime % 60}m  "
                Log.d(javaClass.simpleName, "Run time is $runtime")
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = actionBarTitle

        //set the title bar name

        binding.apply {
            // Set the post title
            movieTitle.text = "${movie.title} (${movie.releaseDate.take(4)})"
            Log.d(javaClass.simpleName, "Run time is now $runtime")
            generalInfo.text = "Release Date: ${movie.releaseDate}"
            popularityScore.text = "Popularity Score: " + movie.popularity.toInt().toString()
            voteAverage.text = "User Score: " + String.format("%.1f", movie.voteAverage)
            onePostSelfText.text = movie.overview

            // Load the post image using Glide
            movie.backdropPath?.let { Glide.glideFetch(it, onePostImage) }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
