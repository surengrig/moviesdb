package app.example.movies.ui.moviedetails

import androidx.lifecycle.*
import app.example.movies.helpers.Result
import app.example.movies.model.Movie
import app.example.movies.repository.moviedetails.MovieDetailsRepository

class MovieDetailsViewModel constructor(
    private val detailsRepository: MovieDetailsRepository
) : ViewModel() {

    var cachedMovie = MutableLiveData<Movie>()
    val details =
        cachedMovie.map {
            detailsRepository.getDetails(it.id).map { result ->
                when (result) {
                    is Result.Success -> result.data
                    else -> {
                        cachedMovie.value
                    }
                }
            }
        }


    fun retry(movieId: Int) {
        detailsRepository.retry(movieId)
    }


}

