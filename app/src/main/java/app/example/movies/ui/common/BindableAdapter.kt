package app.example.movies.ui.common

import app.example.movies.model.NetworkState

/**
 * Interface for binding recyclerview's adapter items and [NetworkState]
 */
interface BindableAdapter<T> {
    fun setItems(items: T)
    fun setNetworkState(networkState: NetworkState?)
}