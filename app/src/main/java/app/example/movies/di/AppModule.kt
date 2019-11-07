package app.example.movies.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.example.movies.BuildConfig
import app.example.movies.repository.moviedetails.MovieDetailsRepository
import app.example.movies.repository.moviedetails.MovieDetailsRepositoryImpl
import app.example.movies.repository.terndingmovies.MoviesRepository
import app.example.movies.repository.terndingmovies.MoviesRepositoryImpl
import app.example.movies.service.ApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Provider
import javax.inject.Singleton


@Module(includes = [AppModule.ProvideViewModel::class])
abstract class AppModule {

    @Module
    class ProvideViewModel {
        private val baseUrl = "https://api.themoviedb.org/3/"
        private val apiKey = BuildConfig.api_key

        @Provides
        @Singleton
        fun provideExecutor(): Executor = Executors.newFixedThreadPool(2)

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .apply {
                    // interceptor for injecting api key
                    addInterceptor(object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val original = chain.request()
                            val httpUrl = original.url
                            val newUrl = httpUrl.newBuilder()
                                .addQueryParameter("api_key", apiKey)
                                .build()
                            val newRequest = original.newBuilder()
                                .url(newUrl)
                                .build()
                            return chain.proceed(newRequest)
                        }
                    })

                    // interceptor for logging
                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.level = HttpLoggingInterceptor.Level.BASIC
                        addInterceptor(logging)
                    }
                }.build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder()
                            .build()
                    )
                )
                .build()

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create()

        @Provides
        @Singleton
        fun provideTrendingRepository(
            apiService: ApiService
        ): MoviesRepository =
            MoviesRepositoryImpl(
                apiService,
                CoroutineScope(Job() + Dispatchers.IO)
            )

        @Provides
        @Singleton
        fun provideDetailsRepository(
            apiService: ApiService
        ): MovieDetailsRepository =
            MovieDetailsRepositoryImpl(
                apiService,
                CoroutineScope(Job() + Dispatchers.IO)
            )


        @Provides
        fun provideViewModelFactory(
            providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
        ): ViewModelProvider.Factory = AppViewModelFactory(providers)


    }

}