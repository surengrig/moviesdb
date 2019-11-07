package app.example.movies.ui.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.paging.PagedList
import app.example.movies.helpers.SingleLiveEvent
import app.example.movies.model.NetworkState
import app.example.movies.model.TrendingMovies
import app.example.movies.repository.terndingmovies.MoviesRepository


class MainViewModel constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    private val listing = moviesRepository.getMoviesList(15)

    val moviesPagedList: LiveData<PagedList<TrendingMovies.Movie>> = liveData {
        emitSource(listing.pagedList)
    }
    val networkState: LiveData<NetworkState> = liveData {
        emitSource(listing.networkState)
    }

    val openMovieLiveData: SingleLiveEvent<Pair<TrendingMovies.Movie, List<View>>> =
        SingleLiveEvent()

    fun retry() {
        moviesRepository.retry()
    }

    fun onMovieClick(movie: TrendingMovies.Movie, vararg views: View) {
        openMovieLiveData.value = Pair(movie, views.asList())
    }
}



