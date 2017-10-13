package com.dev.moviedb.mvvm.data.source.remote

import com.dev.moviedb.model.dto.MovieAggregatorDTO
import com.dev.moviedb.model.dto.MovieDTO

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * This protocol defines the operations over against the web api.
 */
interface MovieApiService {

    @GET("/3/movie/popular")
    fun findMostPopularMovies(@Query("api_key") apiKey: String,
                              @Query("language") lang: String): Call<MovieAggregatorDTO>

    @GET("/3/movie/now_playing")
    fun findNowPlayingMovies(@Query("api_key") apiKey: String,
                             @Query("language") lang: String): Call<MovieAggregatorDTO>

    @GET("/3/movie/upcoming")
    fun findUpcomingMovies(@Query("api_key") apiKey: String,
                           @Query("language") lang: String,
                           @Query("page") page: Int): Call<MovieAggregatorDTO>

    @GET("/3/movie/{id}")
    fun findMovieById(@Path("id") id: Long,
                      @Query("api_key") apiKey: String,
                      @Query("language") lang: String,
                      @Query("append_to_response") whatToAppend: String): Call<MovieDTO>

    @GET("/3/search/movie")
    fun searchMoviesByKeyword(@Query("api_key") apiKey: String,
                              @Query("query") title: String,
                              @Query("language") lang: String,
                              @Query("page") page: Int): Call<MovieAggregatorDTO>


}
