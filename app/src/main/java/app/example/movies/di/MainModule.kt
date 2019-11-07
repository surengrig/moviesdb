package app.example.movies.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.example.movies.repository.terndingmovies.MoviesRepository
import app.example.movies.ui.main.MainFragment
import app.example.movies.ui.main.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [MainModule.ProvideViewModel::class])
abstract class MainModule {

    @ContributesAndroidInjector(modules = [InjectViewModel::class])
    abstract fun bind(): MainFragment

    @Module
    class ProvideViewModel {
        @Provides
        @IntoMap
        @ViewModelKey(MainViewModel::class)
        fun provideMainViewModel(
            moviesRepository: MoviesRepository
        ): ViewModel =
            MainViewModel(moviesRepository)
    }

    @Module
    class InjectViewModel {
        @Provides
        fun provideMainViewModel(
            factory: ViewModelProvider.Factory,
            target: MainFragment
        ) = ViewModelProvider(target, factory)[MainViewModel::class.java]
    }

}