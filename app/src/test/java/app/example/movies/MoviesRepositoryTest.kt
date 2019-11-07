package app.example.movies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import app.example.movies.helpers.Result
import app.example.movies.helpers.getResult
import app.example.movies.model.Listing
import app.example.movies.model.NetworkState
import app.example.movies.model.TrendingMovies
import app.example.movies.repository.terndingmovies.MoviesRepository
import app.example.movies.repository.terndingmovies.MoviesRepositoryImpl
import app.example.movies.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

// Todo switch to runBlockingTest
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MoviesRepositoryTest {
    @Suppress("unused")
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    lateinit var repositorySubject: MoviesRepositoryImpl

    @Mock
    lateinit var mockApi: ApiService
    @Mock
    private lateinit var mockMovie: TrendingMovies.Movie
    @Mock
    private lateinit var mockResponeBody: ResponseBody


    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)

        val scope = TestCoroutineScope(testDispatcher)
        repositorySubject = MoviesRepositoryImpl(
            mockApi, scope
        )
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    /**
     * asserts that empty list works fine
     */
    @Test
    fun emptyList() {
        val listing = repositorySubject.getMoviesList(1)
        val pagedList = getPagedList(listing)
        assertThat(pagedList.size, CoreMatchers.`is`(0))
    }

    /**
     * asserts loading a full list in multiple pages
     */
    @Test
    fun verifyCompleteList() {
        runBlocking {
            val responses = ResponseFactory.listOfResponses(4, mockMovie, mockResponeBody) {
                addSuccessfulResponse()
                addSuccessfulResponse()
                addSuccessfulResponse()
                addSuccessfulResponse()
            }
            `GIVEN responses are`(responses)
            `WHEN loads movies`()
                .`WHEN loads all movies`()
                .`THEN loaded movies are from`(responses)

        }
    }

    /**
     * asserts the failure message when the initial load fails
     */
    @Test
    fun failToLoadInitial() {
        runBlocking {
            `GIVEN responses are`(
                ResponseFactory.listOfResponses(1, mockMovie, mockResponeBody) {
                    addErrorResponse()
                })
            `WHEN loads movies`()
                .`THEN has network error`()

        }
    }

    /**
     * asserts the failure message when the load fails after successful initial load
     */
    @Test
    fun failToLoadAfterInitial() {
        runBlocking {
            `GIVEN responses are`(ResponseFactory.listOfResponses(8, mockMovie, mockResponeBody) {
                addSuccessfulResponse()
                addErrorResponse()
            })
            `WHEN loads movies`().apply {
                `THEN networks state is`(NetworkState.Loaded)
                `WHEN loads all movies`()
                `THEN has network error`()
            }

        }
    }

    /**
     * asserts the retry logic when initial load request fails
     */
    @Test
    fun retryWhenInitialFails() {
        runBlocking {
            val responsesWithError =
                ResponseFactory.listOfResponses(3, mockMovie, mockResponeBody) {
                    addErrorResponse()
                    addSuccessfulResponse()
                    addSuccessfulResponse()
                }
            val responses = ResponseFactory.listOfResponses(3, mockMovie, mockResponeBody) {
                addSuccessfulResponse()
                addSuccessfulResponse()
                addSuccessfulResponse()
            }

            `GIVEN responses are`(responsesWithError)
            `WHEN loads movies`().apply {
                `THEN has network error`()
                `GIVEN responses are`(responsesWithError)
                `WHEN loads all movies`()
                `THEN has network error`()
                `GIVEN responses are`(responses)
                `WHEN retry`(repositorySubject)
                `THEN loaded movies are from`(responses)
            }

        }
    }

    /**
     * asserts the retry logic when load fails after initial load but subsequent loads succeed
     */
    @Test
    fun retryAfterInitialFails() {
        runBlocking {
            val responsesWithError =
                ResponseFactory.listOfResponses(3, mockMovie, mockResponeBody) {
                    addSuccessfulResponse()
                    addErrorResponse()
                    addSuccessfulResponse()
                }
            val responses = ResponseFactory.listOfResponses(3, mockMovie, mockResponeBody) {
                addSuccessfulResponse()
                addSuccessfulResponse()
                addSuccessfulResponse()
            }

            `GIVEN responses are`(responsesWithError)
            `WHEN loads movies`()
                .`THEN loaded movies are from`(listOf(responses[0]))
                .`THEN networks state is`(NetworkState.Loaded)
                .`GIVEN responses are`(responsesWithError)
                .`WHEN loads all movies`()
                .`THEN has network error`()
                .`GIVEN responses are`(responses)
                .`WHEN retry`(repositorySubject)
                .`THEN loaded movies are from`(responses)
        }
    }


    private suspend fun `GIVEN responses are`(response: List<Response<TrendingMovies>?>) =
        givenResponseAre(response)

    private suspend fun Listing<TrendingMovies.Movie>.`GIVEN responses are`(response: List<Response<TrendingMovies>?>): Listing<TrendingMovies.Movie> {
        givenResponseAre(response)
        return this
    }

    private suspend fun givenResponseAre(response: List<Response<TrendingMovies>?>) {
        response.forEachIndexed { index, item ->
            Mockito.`when`(mockApi.moviesList(page = index + 1))
                .thenReturn(item)
        }
    }

    /**
     * Load single page
     */
    private suspend fun `WHEN loads movies`(pageSize: Int = 10): Listing<TrendingMovies.Movie> {
        val listing = repositorySubject.getMoviesList(pageSize = pageSize)
        getPagedList(listing)
        delay(10)
        return listing
    }

    /**
     * Load all pages
     */
    private suspend fun Listing<TrendingMovies.Movie>.`WHEN loads all movies`(): Listing<TrendingMovies.Movie> {
        getPagedList(this).loadAllData()
        delay(10)
        return this
    }

    /**
     * Retry and load all pages
     */
    private suspend fun Listing<TrendingMovies.Movie>.`WHEN retry`(repository: MoviesRepository): Listing<TrendingMovies.Movie> {
        repository.retry()
        delay(10)
        this.pagedList.value?.loadAllData()
        delay(10)
        return this
    }

    /**
     * Asserts all the movies are loaded from [responses]
     */
    private fun Listing<TrendingMovies.Movie>.`THEN loaded movies are from`(responses: List<Response<TrendingMovies>>): Listing<TrendingMovies.Movie> {
        val posts = responses.map {
            val result = it.getResult()
            when (result) {
                is Result.Success -> result.data.results
                else -> null
            }
        }.filterNotNull()
            .reduce { acc, list ->
                val newList = mutableListOf<TrendingMovies.Movie>()
                newList.addAll(acc)
                newList.addAll(list)
                newList
            }
        val actual: MutableList<TrendingMovies.Movie>? = this.pagedList.value?.snapshot()
        assertThat(
            actual,
            CoreMatchers.`is`(posts)
        )
        return this
    }

    private fun Listing<TrendingMovies.Movie>.`THEN has network error`(): Listing<TrendingMovies.Movie> {
        assert(getNetworkState(this) is NetworkState.Failure)
        return this
    }

    private fun Listing<TrendingMovies.Movie>.`THEN networks state is`(networkState: NetworkState): Listing<TrendingMovies.Movie> {
        assertThat(
            getNetworkState(this),
            CoreMatchers.`is`(networkState)
        )
        return this
    }


    /**
     * extract the latest paged list from the listing
     */
    private fun getPagedList(listing: Listing<TrendingMovies.Movie>): PagedList<TrendingMovies.Movie> {
        val observer = LoggingObserver<PagedList<TrendingMovies.Movie>>()
        listing.pagedList.observeForever(observer)
        assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        return observer.value!!
    }

    /**
     * extract the latest network state from the listing
     */
    private fun getNetworkState(listing: Listing<TrendingMovies.Movie>): NetworkState? {
        val networkObserver = LoggingObserver<NetworkState>()
        listing.networkState.observeForever(networkObserver)
        return networkObserver.value
    }


    private fun <T> PagedList<T>.loadAllData() {
        if (size == 0) return
        do {
            val oldSize = this.loadedCount
            this.loadAround(this.size - 1)
        } while (this.size != oldSize)
    }

    /**
     * simple observer that logs the latest value it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        var value: T? = null
        override fun onChanged(t: T?) {
            this.value = t
        }
    }
}



