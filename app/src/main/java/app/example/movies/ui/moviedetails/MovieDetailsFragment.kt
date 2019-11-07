package app.example.movies.ui.moviedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.example.movies.R
import app.example.movies.databinding.FragmentMovieDetailsBinding
import app.example.movies.helpers.toMovie
import app.example.movies.model.TrendingMovies
import app.example.movies.ui.common.DaggerFragmentX
import kotlinx.android.synthetic.main.fragment_movie_details.*
import javax.inject.Inject


class MovieDetailsFragment : DaggerFragmentX() {
    @Inject
    lateinit var viewModel: MovieDetailsViewModel

    private lateinit var binding: FragmentMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movie: TrendingMovies.Movie? = arguments?.getParcelable(MOVIE_EXTRA)
        viewModel.cachedMovie.value = movie?.toMovie()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()

        binding.viewModel = viewModel
    }


    private fun setupToolbar() {
        val toolbar = toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    companion object {
        const val MOVIE_EXTRA = "movie_extra"
        fun newInstance(movie: TrendingMovies.Movie) = MovieDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(MOVIE_EXTRA, movie)
            }
        }
    }

}


