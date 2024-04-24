package edu.utap.movieapp.ui.review

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import edu.utap.movieapp.MainViewModel
import edu.utap.movieapp.R
import edu.utap.movieapp.api.MovieResponse
import edu.utap.movieapp.databinding.ReviewEditBinding
import edu.utap.movieapp.glide.Glide

class ReviewEdit :
    Fragment(R.layout.review_edit) {
    // TODO: This should really be part of view model to allow
    // navigation away from in progress note editing
    // A little bit of a pain in the butt that this is not a MutableList
    // but the ListAdapter needs two lists with distinct references
    // to work properly.  So either hassle with List or make it
    // a MutableList and then have to call notify* on adapter.
    private lateinit var selectedMovie: MovieResponse
    private var position = -1
    private val viewModel: MainViewModel by activityViewModels()
    private val args: ReviewEditArgs by navArgs()

    private lateinit var movieTitleDropdown: Spinner
    private lateinit var movieTitles: List<String>


    // Get rid of image roll menu icon
    private fun initMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // No image roll menu in image roll fragment
                menu.clear()
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner)
    }
    // No need for onCreateView because we passed R.layout to Fragment constructor
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = ReviewEditBinding.bind(view)
        initMenu()
        initMovieTitleDropdown(binding.reviewMoviePoster) // update the spinner
        position = args.position
        if(position != -1) {
            val review = viewModel.getReview(position)
            binding.reviewText.text.insert(0, review.text)
            viewModel.observePlayingMovies().observe(viewLifecycleOwner) { movies ->
                movieTitles = movies.map { it.title }
                movieTitleDropdown.setSelection(movieTitles.indexOf(review.movieTitle))
            }
        }

        // Put cursor in edit text
        binding.reviewText.requestFocus()

        binding.reviewText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                binding.characterCount.text = "$length/280"
            }
            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }
        })

        binding.saveButton.setOnClickListener {
            val inputText = binding.reviewText.text.toString()
            if (movieTitleDropdown.isEmpty()) {
                Toast.makeText(activity,
                    "Please choose a movie to review!",
                    Toast.LENGTH_LONG).show()
                }
            if (inputText.isEmpty()) {
                Toast.makeText(activity,
                        "Enter review!",
                        Toast.LENGTH_LONG).show()
            } else {
                if(position == -1) {
                    Log.d(javaClass.simpleName, "create review len ${selectedMovie} pos $position")
                    viewModel.createReview(inputText, selectedMovie)
                } else {
                    Log.d(javaClass.simpleName, "update list len ${selectedMovie} pos $position")
                    viewModel.updateReview(position, inputText, selectedMovie)
                }
                findNavController().popBackStack()
            }
        }
        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initMovieTitleDropdown(posterImageView: ImageView) {
        movieTitleDropdown = view?.findViewById(R.id.movie_title_dropdown)!!
        // Observe the movieItems LiveData from the MainViewModel
        viewModel.observePlayingMovies().observe(viewLifecycleOwner) { movies ->
            movieTitles = movies.map { it.title }
            Log.d(javaClass.simpleName, "list of playing movie is  ${movieTitles}")
            // Set up the dropdown adapter
            val adapter =
                ArrayAdapter(requireContext(), R.layout.spinner_item, movieTitles)
            adapter.setDropDownViewResource(R.layout.spinner_item)
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            movieTitleDropdown.adapter = adapter
            // Set the custom background drawable programmatically
            movieTitleDropdown.background = ContextCompat.getDrawable(requireContext(), R.drawable.spinner_border)
        }
        movieTitleDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedMovie = viewModel.observePlayingMovies().value!![position]
                // Handle the selected movie, e.g., update the review data or perform any other action
                Glide.glideFetch(selectedMovie.posterPath, posterImageView)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Nothing
            }

        }
    }

}