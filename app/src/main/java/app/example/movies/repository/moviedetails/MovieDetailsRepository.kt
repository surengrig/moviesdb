package app.example.movies.repository.moviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.example.movies.helpers.Result
import app.example.movies.helpers.getResult
import app.example.movies.model.Movie
import app.example.movies.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


interface MovieDetailsRepository {
    fun getDetails(movieId: Int): LiveData<Result<Movie>>

    fun retry(movieId: Int)
}


/**
 * Repository implementation for getting movie details.
 */
class MovieDetailsRepositoryImpl(
    private val apiService: ApiService,
    private val coroutineScope: CoroutineScope
) : MovieDetailsRepository {
    private var detailsLiveData = MutableLiveData<Result<Movie>>()

    override fun retry(movieId: Int) {
        coroutineScope.launch {
            try {
                val result = apiService.movie(movieId).getResult()
                detailsLiveData.postValue(result)
            } catch (ex: Exception) {
                detailsLiveData.postValue(Result.Failure(ex))
            }
        }
    }

    /**
     * Should only call this function once
     */
    override fun getDetails(movieId: Int): LiveData<Result<Movie>> {
        detailsLiveData = MutableLiveData()
        retry(movieId)
        return detailsLiveData
    }


}

