package app.example.movies.model

sealed class NetworkState {
    object Loading : NetworkState()
    object Loaded : NetworkState()
    class Failure(val error: Throwable?) : NetworkState()
}
