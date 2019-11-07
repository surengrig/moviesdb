package app.example.movies.service

import app.example.movies.model.Movie
import app.example.movies.model.TrendingMovies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("trending/{media_type}/{time_window}")
    suspend fun moviesList(
        @Path(value = "media_type") mediaType: String = "all",
        @Path(value = "time_window") timeWindow: String = "week",
        @Query(value = "page") page: Int = 1
    ): Response<TrendingMovies>

    @GET("movie/{movie_id}")
    suspend fun movie(@Path(value = "movie_id") movieId: Int): Response<Movie>

}
