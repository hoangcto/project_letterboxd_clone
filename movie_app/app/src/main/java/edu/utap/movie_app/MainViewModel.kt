package edu.utap.movie_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.movie_app.api.MovieItemRepository
import edu.utap.movie_app.api.MovieResponse
import edu.utap.movie_app.api.TMDBApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)
    private val api = TMDBApi.create()
    private val repository = MovieItemRepository(api)
    private val movieItems = MutableLiveData<List<MovieResponse>>()

    private var currentAuthUser = invalidUser

//    private val _movies = MediatorLiveData<List<MovieResponse>>()
//    val movies: LiveData<List<MovieResponse>> get() = _movies

    init {
        Log.d("MainViewModel", "ViewModel initialized")
//        fetchPopularMovies()
    }


    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

//    private fun fetchPopularMovies() {
//        viewModelScope.launch {
//            try {
//                val popularMovies = repository.getPopularMovies()
//                _movies.value = popularMovies
//            } catch (e: Exception) {
//                Log.e("MainViewModel", "Error fetching popular movies fetchPop", e)
//                // Handle the error case, e.g., show an error message to the user
//            }
//        }
//    }

    fun fetchPopularMovies() {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
                try {
                    val movies = repository.getPopularMovies()
                    movieItems.postValue(repository.getPopularMovies())
                    fetchDone.postValue(true)
                    Log.d("MainViewModel", "Fetched ${movies.size} popular movies")
                    Log.d("MainViewModel", "Fetched $movies. popular movies")
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error fetching popular movies netMovies", e)
                    // Handle the error case, e.g., show an error message to the user
                }
        }
    }


    fun observeMovies(): LiveData<List<MovieResponse>> {
        return movieItems
    }



    fun repoFetch() {
        TODO("Not yet implemented")
    }

}