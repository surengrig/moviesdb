package app.example.movies.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import app.example.movies.R
import app.example.movies.databinding.FragmentMainBinding
import app.example.movies.model.TrendingMovies
import app.example.movies.ui.common.DaggerFragmentX
import app.example.movies.ui.moviedetails.MovieDetailsFragment
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : DaggerFragmentX() {
    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        setupToolbar()
        setupObservers()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager
        recyclerView.hasFixedSize()
        val adapter = MoviesListAdapter(
            viewModel
        )
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    private fun setupObservers() {
        viewModel.openMovieLiveData.observe(this) {
            val (movie, transitionViews) = it
            openDetailsFragment(movie, transitionViews)
        }
    }

    private fun openDetailsFragment(
        movie: TrendingMovies.Movie,
        transitionViews: List<View>
    ) {
        activity?.supportPostponeEnterTransition()

        val fade = TransitionInflater.from(activity).inflateTransition(
            android.R.transition.fade
        )
        val moveTransition = TransitionInflater.from(activity).inflateTransition(
            android.R.transition.move
        )

        sharedElementReturnTransition = moveTransition

        val fragment = MovieDetailsFragment.newInstance(movie)
        fragment.sharedElementEnterTransition = moveTransition
        fragment.enterTransition = fade

        activity?.supportFragmentManager?.commit {
            replace(
                android.R.id.content,
                fragment,
                "movie_details"
            )
            setReorderingAllowed(true)
            transitionViews.forEach { view ->
                addSharedElement(view, view.transitionName)
            }
            addToBackStack("movies")
        }
    }
}