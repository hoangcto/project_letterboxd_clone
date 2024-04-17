package edu.utap.movie_app.api

import android.util.Log
import android.util.MalformedJsonException
import com.google.gson.Gson
import edu.utap.movie_app.MainActivity

class MovieItemRepository(private val tmdbApi: TMDBApi) {

    suspend fun getPopularMovies(): List<MovieResponse> {
        val apiKey = "d743256e1d9a2a50cebe15545e780ea8"
        val page = 1
        val language = "en-US"
        Log.d("MovieItemRepository", "Fetching movies for app")
        val response : ListingResponse
        try {
            if (MainActivity.globalDebug) {
                Log.d("MovieItemRepository", "JSON response: " + MainActivity.jsonAww100)
                response = Gson().fromJson(
                    MainActivity.jsonAww100,
                    ListingResponse::class.java)
            } else {
                // XXX Write me.
                response = tmdbApi.getPopularMovies(apiKey = apiKey, language, page)
                val gson = Gson()
                val jsonResponse = gson.toJson(response)
                Log.d("MovieItemRepository", "JSON response: $response")
                Log.d("MovieItemRepository", "API Response: $jsonResponse")
            }
            return response.movies
        } catch (e: MalformedJsonException) {
            Log.e("MovieItemRepository", "Failed to parse JSON response", e)
            // Display an error message or take appropriate action
            return emptyList()
        }
    }
    suspend fun getMovieDetails(movieId: Int): MovieResponse? {
        val apiKey = "YOUR_API_KEY"
        val language = "en-US"
        return try {
            tmdbApi.getMovieDetails(movieId, apiKey, language)
        } catch (e: Exception) {
            Log.e("MovieItemRepository", "Failed to fetch movie details", e)
            null
        }
    }

    suspend fun searchMovies(query: String, page: Int): List<MovieResponse> {
        val apiKey = "YOUR_API_KEY"
        val language = "en-US"
        return try {
            val response = tmdbApi.searchMovies(apiKey, query, language, page)
            response.movies
        } catch (e: Exception) {
            Log.e("MovieItemRepository", "Failed to search for movies", e)
            emptyList()
        }
    }
}