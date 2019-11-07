package app.example.movies.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.example.movies.BR
import app.example.movies.R
import app.example.movies.model.NetworkState
import app.example.movies.model.TrendingMovies
import app.example.movies.ui.common.BindableAdapter

class MoviesListAdapter(
    val viewModel: ViewModel
) : PagedListAdapter<TrendingMovies.Movie, RecyclerView.ViewHolder>(DIFF_CALLBACK),
    BindableAdapter<PagedList<TrendingMovies.Movie>?> {
    private var networkState: NetworkState? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater, viewType, parent, false
        )

        return when (viewType) {
            R.layout.movie_item_view -> MovieViewHolder(binding)
            R.layout.network_state_item -> NetworkStateViewHolder(binding)
            R.layout.loading_item -> NetworkStateViewHolder(binding)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (getItemViewType(position)) {
            R.layout.movie_item_view -> {
                val item = getItem(position)
                (holder as MovieViewHolder).bindItem(item, viewModel)
            }
            R.layout.network_state_item -> (holder as NetworkStateViewHolder).bind(viewModel)
            R.layout.loading_item -> (holder as NetworkStateViewHolder).bind(viewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            when (networkState) {
                is NetworkState.Failure -> R.layout.network_state_item
                is NetworkState.Loading -> R.layout.loading_item
                else -> throw IllegalStateException()
            }
        } else {
            R.layout.movie_item_view
        }
    }


    override fun getItemCount(): Int {
        return if (hasExtraRow()) {
            super.getItemCount() + 1
        } else {
            super.getItemCount()
        }
    }

    override fun getItemId(position: Int) = position.toLong()

    /**
     * Sets [NetworkState] and inserts or removed additional item for showing the state
     */
    override fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        val prevItemCount = itemCount
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(prevItemCount)
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    override fun setItems(items: PagedList<TrendingMovies.Movie>?) {
        if (items != null) submitList(items)
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.Loaded

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingMovies.Movie>() {
            override fun areItemsTheSame(
                oldItem: TrendingMovies.Movie,
                newItem: TrendingMovies.Movie
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TrendingMovies.Movie,
                newItem: TrendingMovies.Movie
            ): Boolean = oldItem == newItem
        }
    }
}


class MovieViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindItem(item: TrendingMovies.Movie?, viewModel: ViewModel) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }
}

class NetworkStateViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }
}
