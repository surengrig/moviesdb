package app.example.movies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.example.movies.helpers.Result
import app.example.movies.model.Movie
import app.example.movies.repository.moviedetails.MovieDetailsRepositoryImpl
import app.example.movies.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MovieDetailsRepositoryTest {
    @Suppress("unused")
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var repositorySubject: MovieDetailsRepositoryImpl
    private lateinit var errorResponse: Response<Movie>

    @Mock
    private lateinit var mockResponeBody: ResponseBody
    @Mock
    private lateinit var mockApi: ApiService


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        errorResponse = Response.error<Movie>(401, mockResponeBody)

        val scope = TestCoroutineScope(testDispatcher)
        repositorySubject = MovieDetailsRepositoryImpl(
            mockApi, scope
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * When response is success
     * Emits [Result.Success]
     */
    @Test
    fun getDetailsSuccessTest() {
        runBlocking {
            val movieId = 19
            `GIVEN response is`(movieId, Response.success(Movie(id = movieId)))
            val observer = `WHEN loads movie details`(movieId)
            assert(observer.value is Result.Success)

            val actual = observer.value as Result.Success<Movie>
            val expected = Result.Success(Movie(id = movieId))
            assertThat(actual, CoreMatchers.`is`(expected))
        }
    }

    /**
     * When there is an error, emits [Result.Failure]
     */
    @Test
    fun getDetailsFailureTest() = runBlockingTest {
        val movieId = 19
        `GIVEN response is`(movieId, errorResponse)
        `WHEN loads movie details`(movieId).apply {
            `THEN emits Failure`()
        }
    }

    /**
     * Given there was an error
     * When retry and the response is Success
     * Then emits [Result.Success]
     */
    @Test
    fun retryAfterFailTest() = runBlockingTest {
        val movieId = 19
        `GIVEN response is`(movieId, errorResponse)
        `WHEN loads movie details`(movieId).apply {
            `THEN emits Failure`()

            `GIVEN response is`(movieId, Response.success(Movie(id = movieId)))
            `WHEN retry`(movieId)
            `THEN emits Success`(movieId)
        }
    }

    /**
     * Given loaded successfully
     * When retry and the response is error
     * Then emits [Result.Failure]
     */
    @Test
    fun retryAfterSuccessTest() = runBlockingTest {
        val movieId = 19
        `GIVEN response is`(movieId, Response.success(Movie(id = movieId)))
        `WHEN loads movie details`(movieId).apply {
            `THEN emits Success`(movieId)
            `GIVEN response is`(movieId, errorResponse)
            `WHEN retry`(movieId)
            `THEN emits Failure`()
        }
    }

    private fun <T> LoggingObserver<T>.`WHEN retry`(movieId: Int): LoggingObserver<T> {
        repositorySubject.retry(movieId)
        return this
    }

    private fun `WHEN loads movie details`(movieId: Int): LoggingObserver<Result<Movie>> {
        val details = repositorySubject.getDetails(movieId)
        val observer = LoggingObserver<Result<Movie>>()
        details.observeForever(observer)
        return observer
    }

    private suspend fun `GIVEN response is`(id: Int, response: Response<Movie>) =
        Mockito.`when`(mockApi.movie(id))
            .thenReturn(response)


    private fun LoggingObserver<Result<Movie>>.`THEN emits Failure`() {
        assert(value is Result.Failure)
    }

    private fun LoggingObserver<Result<Movie>>.`THEN emits Success`(
        movieId: Int
    ) {
        assert(value is Result.Success)

        val actual = value as Result.Success<Movie>
        val expected = Result.Success(Movie(id = movieId))
        assertThat(actual, CoreMatchers.`is`(expected))
    }

}


