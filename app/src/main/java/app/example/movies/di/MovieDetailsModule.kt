package app.example.movies.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.example.movies.repository.moviedetails.MovieDetailsRepository
import app.example.movies.ui.moviedetails.MovieDetailsFragment
import app.example.movies.ui.moviedetails.MovieDetailsViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap


@Module(includes = [MovieDetailsModule.ProvideViewModel::class])
abstract class MovieDetailsModule {

    @ContributesAndroidInjector(modules = [InjectViewModel::class])
    abstract fun bind(): MovieDetailsFragment

    @Module
    class ProvideViewModel {
        @Provides
        @IntoMap
        @ViewModelKey(MovieDetailsViewModel::class)
        fun provideMainViewModel(
            moviesRepository: MovieDetailsRepository
        ): ViewModel =
            MovieDetailsViewModel(moviesRepository)
    }

    @Module
    class InjectViewModel {
        @Provides
        fun provideMainViewModel(
            factory: ViewModelProvider.Factory,
            target: MovieDetailsFragment
        ) = ViewModelProvider(target, factory)[MovieDetailsViewModel::class.java]
    }

}