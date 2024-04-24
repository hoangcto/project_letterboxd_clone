package edu.utap.movieapp.api

import android.content.pm.PackageManager
import android.util.Log
import android.util.MalformedJsonException
import com.google.gson.Gson
import edu.utap.movieapp.MainActivity


class MovieItemRepository(private val tmdbApi: TMDBApi) {

    suspend fun getPopularMovies(): List<MovieResponse> {
        val apiKey = Constants.API_KEY
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

    suspend fun getPlayingMovies(): List<MovieResponse> {
        val apiKey = Constants.API_KEY
        val page = 1
        val language = "en-US"
        val response : ListingResponse
        if (MainActivity.globalDebug) {
            Log.d("MovieItemRepository", "JSON response: " + MainActivity.jsonAww100)
            response = Gson().fromJson(
                MainActivity.jsonAww100,
                ListingResponse::class.java)
        } else {
            response = tmdbApi.getPlayingMovies(apiKey = apiKey, language, page)
            val gson = Gson()
            val jsonResponse = gson.toJson(response)
            Log.d("MovieItemRepository", "JSON response: $response")
            Log.d("MovieItemRepository", "API Response: $jsonResponse")
        }
        return response.movies
    }

    suspend fun getTopMovies(): List<MovieResponse> {
        val apiKey = Constants.API_KEY
        val page = 1
        val language = "en-US"
        val response : ListingResponse
        if (MainActivity.globalDebug) {
            Log.d("MovieItemRepository", "JSON response: " + MainActivity.jsonAww100)
            response = Gson().fromJson(
                MainActivity.jsonAww100,
                ListingResponse::class.java)
        } else {
            response = tmdbApi.getTopMovies(apiKey = apiKey, language, page)
            val gson = Gson()
            val jsonResponse = gson.toJson(response)
            Log.d("MovieItemRepository", "JSON response: $response")
            Log.d("MovieItemRepository", "API Response: $jsonResponse")
        }
        return response.movies
    }



    suspend fun getMovieDetails(movieId: Int): MovieResponse {
        val apiKey = Constants.API_KEY
        val language = "en-US"
        return tmdbApi.getMovieDetails(movieId, apiKey, language)

    }



}