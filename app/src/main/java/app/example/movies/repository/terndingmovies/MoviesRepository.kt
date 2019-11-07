package app.example.movies.repository.terndingmovies

import androidx.annotation.MainThread
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import app.example.movies.model.Listing
import app.example.movies.model.TrendingMovies
import app.example.movies.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


interface MoviesRepository {
    fun getMoviesList(pageSize: Int): Listing<TrendingMovies.Movie>

    fun retry()
}

class MoviesRepositoryImpl(
    apiService: ApiService,
    private val coroutineScope: CoroutineScope
) : MoviesRepository {

    private val dataSourceFactory =
        DataSourceFactory(apiService)

    @MainThread
    override fun getMoviesList(pageSize: Int): Listing<TrendingMovies.Movie> {

        val trendingMoviesList = dataSourceFactory.toLiveData(
            config = PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setEnablePlaceholders(true)
                .build()
        )
        val networkState = Transformations.switchMap(dataSourceFactory.sourceLiveData) {
            it.networkState
        }

        return Listing(
            pagedList = trendingMoviesList,
            networkState = networkState
        )
    }

    override fun retry() {
        coroutineScope.launch {
            dataSourceFactory.sourceLiveData.value?.retryAllFailed()
        }
    }
}
