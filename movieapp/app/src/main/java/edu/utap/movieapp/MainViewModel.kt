package edu.utap.movieapp

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.movieapp.api.MovieItemRepository
import edu.utap.movieapp.api.MovieResponse
import edu.utap.movieapp.api.TMDBApi
import edu.utap.movieapp.model.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    //////////////////////////
    // this is the API portion
    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)
    private val api = TMDBApi.create()
    private val repository = MovieItemRepository(api)
    private val movieItems = MutableLiveData<List<MovieResponse>>()
    private val movieNowPlayingItems = MutableLiveData<List<MovieResponse>>()
    private val movieDetails = MutableLiveData<MovieResponse?>()
    var currentButton = 2
    private var currentAuthUser = invalidUser

    ////////////////
    // LiveData for entire review list, all images
    private var reviewsList = MutableLiveData<List<Review>>()
    private var reviewsEmpty = MediatorLiveData<Boolean>().apply {
        addSource(reviewsList) {
            this.value = it.isNullOrEmpty()
        }
    }
    // Database access
    private val dbHelp = ViewModelDBHelper()


    init {
        Log.d("MainViewModel", "ViewModel initialized")
        fetchPlayingMovies()
        fetchInitialReviews() {} //nothing to call back for
    }

    /// USER INFORMATION
    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

    fun getCurrentUserUid(): String {
        return currentAuthUser.uid
    }

    fun fetchPopularMovies() {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
                try {
                    val movies = repository.getPopularMovies()
                    movieItems.postValue(movies)
                    fetchDone.postValue(true)
                    Log.d("MainViewModel", "Fetched ${movies.size} popular movies")
                    Log.d("MainViewModel", "Fetched $movies. popular movies")
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error fetching popular movies netMovies", e)
                    // Handle the error case, e.g., show an error message to the user
                }
        }
    }

    fun fetchPlayingMovies() {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                val movies = repository.getPlayingMovies()
                movieItems.postValue(movies)
                movieNowPlayingItems.postValue(movies)
                fetchDone.postValue(true)
                Log.d("MainViewModel", "Fetched ${movies.size} popular movies")
                Log.d("MainViewModel", "Fetched $movies. popular movies")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching playing movies netMovies", e)
                // Handle the error case, e.g., show an error message to the user
            }
        }
    }

    fun fetchTopMovies() {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                val movies = repository.getTopMovies()
                movieItems.postValue(movies)
                fetchDone.postValue(true)
                Log.d("MainViewModel", "Fetched ${movies.size} top-rated movies")
                Log.d("MainViewModel", "Fetched $movies. top-rated movies")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching top-rated movies", e)
                // Handle the error case, e.g., show an error message to the user
            }
        }
    }

    fun fetchMovieDetails(movieID: Int) {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val details = repository.getMovieDetails(movieID)
            Log.d("MainViewModel", "Fetched details $details")
            movieDetails.postValue(details)
        }
    }

    fun observeMovieDetails(): MutableLiveData<MovieResponse?> {
        return movieDetails
    }

    fun observeMovies(): LiveData<List<MovieResponse>> {
        return movieItems
    }

    fun observePlayingMovies(): LiveData<List<MovieResponse>> {
        return movieNowPlayingItems
    }


    fun repoFetch() {
        TODO("Not yet implemented")
    }

    //////////////////////////////////////
    // this is the Reviews, memory cache and database interaction

    fun fetchInitialReviews(callback: ()->Unit) {
        dbHelp.fetchInitialReviews(reviewsList, callback)
    }
    fun observeReviews(): LiveData<List<Review>> {
        return reviewsList
    }
    fun observeReviewsEmpty(): LiveData<Boolean> {
        return reviewsEmpty
    }
    // Get a note from the memory cache
    fun getReview(position: Int) : Review {
        val note = reviewsList.value?.get(position)
        Log.d(javaClass.simpleName, "notesList.value ${reviewsList.value}")
        Log.d(javaClass.simpleName, "getNode $position list len ${reviewsList.value?.size}")
        return note!!
    }
    // After we successfully modify the db, we refetch the contents to update our
    // live data.  Hence we always pass in notesList
    fun updateReview(position: Int, text: String, selectedMovie: MovieResponse) {
        val review = getReview(position)
        // Have to update text before calling updateNote
        review.text = text
        review.poster_path = selectedMovie.posterPath
        review.movieTitle = selectedMovie.title
        review.releaseDate = selectedMovie.releaseDate
        dbHelp.updateReview(review, reviewsList)
    }

    fun createReview(text: String, selectedMovie: MovieResponse) {
        val currentUser = currentAuthUser
        val review = Review(
            name = currentUser.name,
            ownerUid = currentUser.uid,
            text = text,
            poster_path = selectedMovie.posterPath,
            movieTitle = selectedMovie.title,
            releaseDate = selectedMovie.releaseDate
            // database sets firestoreID
        )
        dbHelp.createReview(review, reviewsList)
        fetchInitialReviews() {}
    }
    fun removeReviewAt(position: Int) {
        //SSS
        val review = getReview(position)
        //EEE // XXX What do to before we delete note?
        Log.d(javaClass.simpleName, "remote review at pos: $position id: ${review.firestoreID}")
        dbHelp.removeReview(review, reviewsList)
    }

}