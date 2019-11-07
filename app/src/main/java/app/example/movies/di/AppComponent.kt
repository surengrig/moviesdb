package app.example.movies.di

import app.example.movies.MoviesApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        MainModule::class,
        MovieDetailsModule::class
    ]
)

interface AppComponent : AndroidInjector<MoviesApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: MoviesApp): Builder

        fun build(): AppComponent
    }
}
