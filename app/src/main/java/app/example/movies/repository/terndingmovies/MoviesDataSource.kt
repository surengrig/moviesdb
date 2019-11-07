package app.example.movies.repository.terndingmovies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import app.example.movies.helpers.Result
import app.example.movies.helpers.getResult
import app.example.movies.model.NetworkState
import app.example.movies.model.TrendingMovies
import app.example.movies.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A data source that uses the page index returned for page requests.
 */
class MoviesDataSource(
    private val api: ApiService,
    private val coroutineScope: CoroutineScope
) : PageKeyedDataSource<Int, TrendingMovies.Movie>() {

    private val _networkState = MutableLiveData<NetworkState>()
    /**
     * LiveData for [NetworkState]
     */
    val networkState: LiveData<NetworkState>
        get() = _networkState

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    /**
     * Retries api request
     */
    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, TrendingMovies.Movie>
    ) {
        _networkState.postValue(NetworkState.Loading)

        coroutineScope.launch {
            retry = {
                loadInitial(params, callback)
            }
            try {
                val response = api.moviesList(page = 1).getResult()

                when (response) {
                    is Result.Success -> {
                        retry = null
                        _networkState.postValue(NetworkState.Loaded)

                        val items = response.data.results
                        val totalPages = response.data.totalPages

                        if (totalPages > 1) {
                            callback.onResult(items, null, 2)
                        } else {
                            callback.onResult(items, null, null)
                        }
                    }
                    is Result.Failure -> {
                        _networkState.postValue(NetworkState.Failure(response.error))
                    }
                }
            } catch (ex: Exception) {
                _networkState.postValue(NetworkState.Failure(ex))
            }

        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, TrendingMovies.Movie>
    ) {
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, TrendingMovies.Movie>
    ) {
        _networkState.postValue(NetworkState.Loading)

        coroutineScope.launch {
            try {
                val response = api.moviesList(
                    page = params.key
                ).getResult()

                when (response) {
                    is Result.Success -> {
                        retry = null
                        _networkState.postValue(NetworkState.Loaded)

                        val items = response.data.results
                        val totalPages = response.data.totalPages
                        val page = response.data.page

                        if (page < totalPages) {
                            callback.onResult(items, page + 1)
                        } else {
                            callback.onResult(items, null)
                        }
                    }
                    is Result.Failure -> {
                        _networkState.postValue(NetworkState.Failure(response.error))
                        retry = {
                            loadAfter(params, callback)
                        }
                    }
                }
            } catch (ex: Exception) {
                retry = {
                    loadAfter(params, callback)
                }
                _networkState.postValue(NetworkState.Failure(ex))
            }

        }
    }
}