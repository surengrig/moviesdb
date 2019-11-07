package app.example.movies

import app.example.movies.model.TrendingMovies
import okhttp3.ResponseBody
import retrofit2.Response

class ResponseFactory(
    private val totalPages: Int,
    private val movie: TrendingMovies.Movie,
    private val responseBody: ResponseBody
) {
    companion object {
        fun listOfResponses(
            totalPages: Int,
            movie: TrendingMovies.Movie,
            responseBody: ResponseBody,
            invoke: ResponseFactory.() -> Unit
        ): List<Response<TrendingMovies>> {
            val factory = ResponseFactory(totalPages, movie, responseBody)
            factory.invoke()
            return factory.responses
        }
    }

    private var index = 1
    private fun fakeResponse(id: Int): List<TrendingMovies.Movie> {
        return listOf(movie.copy(id))
    }

    private val _responses = arrayListOf<Response<TrendingMovies>>()
    val responses: List<Response<TrendingMovies>>
        get() = _responses

    private fun addResponse(
        status: Int = 200,
        page: Int = 1,
        results: List<TrendingMovies.Movie> = emptyList(),
        totalPages: Int = 1,
        totalResults: Int = 0
    ) {
        if (status == 200) {
            index++
        }

        val response = Response.success(
            TrendingMovies(
                page = page,
                results = results,
                totalResults = totalResults,
                totalPages = totalPages
            )
        )
        _responses.add(response)
    }

    fun addSuccessfulResponse() {
        addResponse(
            results = fakeResponse(index),
            status = 200,
            totalPages = totalPages,
            page = index
        )
    }

    fun addErrorResponse() {
        val response = Response.error<TrendingMovies>(401, responseBody)
        _responses.add(response)
    }
}