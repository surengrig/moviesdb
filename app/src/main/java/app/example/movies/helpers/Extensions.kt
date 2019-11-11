package app.example.movies.helpers

import app.example.movies.model.Movie
import app.example.movies.model.TrendingMovies
import retrofit2.HttpException
import retrofit2.Response

fun <T : Any> Response<T>.getResult(): Result<T> {
    return if (this.isSuccessful) {
        val body = this.body()

        if (body == null) {
            Result.Failure(Exception("empty response"))
        } else {
            Result.Success(body)
        }
    } else {
        Result.Failure(HttpException(this))
    }
}

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    object Loading : Result<Nothing>()
    object Loaded : Result<Nothing>()
    data class Failure(val error: Throwable?) : Result<Nothing>()
}

fun TrendingMovies.Movie.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview
    )
}