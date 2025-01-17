package com.dev.moviedb.mvvm.moviesTab

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.dev.moviedb.YamdaApplication
import com.dev.moviedb.mvvm.extensions.formatMovieCardName
import com.dev.moviedb.mvvm.extensions.loadPosterUrl
import com.dev.moviedb.mvvm.fragments.AbstractDisplayFragment
import com.dev.moviedb.mvvm.movieDetails.MovieDetailsActivity
import com.dev.moviedb.mvvm.repository.NowPlayingMovieRepository
import com.dev.moviedb.mvvm.repository.PopularMovieRepository
import com.dev.moviedb.mvvm.repository.TopRatedMovieRepository
import com.dev.moviedb.mvvm.repository.remote.dto.MovieCollectionDTO
import com.dev.moviedb.mvvm.repository.remote.dto.MovieDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_movies_tab_layout.*
import kotlinx.android.synthetic.main.include_item_spotlight.*
import petegabriel.com.yamda.R


/**
 * Tab fragment that shows the different information for imageList inside the application.
 *
 * Yamda 1.1.0.
 */
class MoviesTabFragment : AbstractDisplayFragment() {

    override fun getLoggingTag(): String = this.javaClass.canonicalName ?: "MoviesTabFragment"

    /**
     * A reference to the view model class
     */
    private var viewModel: MoviesTabViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val app = activity?.applicationContext as YamdaApplication
        val popularRepo = PopularMovieRepository(app.apiService)
        val topRatedRepo = TopRatedMovieRepository(app.apiService)
        val nowPlayingRepo = NowPlayingMovieRepository(app.apiService)
        viewModel =  MoviesTabViewModel(app.apiService, popularRepo, topRatedRepo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO this must be better handled
        thirdCardView.visibility = View.GONE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscribeToTheMostRecentMovie()
        subscribeToMostPopularMovies()
        subscribeToTopRatedMovies()
    }


    override fun handleItemClick(): (MovieDTO) -> Unit {
        return { m ->
            run {
                viewModel?.findMovieById(m.id.toLong(), "credits,videos,images")
                        ?.subscribeOn(Schedulers.newThread())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe({ t ->
                            run {
                                navigateToDetailsVIew(t)
                            }
                        }, { throwable ->
                            handleError(throwable)
                        })
            }
        }
    }

    private fun navigateToDetailsVIew(t: MovieDTO?) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        val b = Bundle()
        b.putParcelable(MovieDetailsActivity.ITEM_ARGS_KEY, t)
        intent.putExtras(b) //Put your id to your next Intent
        startActivity(intent)
        //set the animation of the exiting and entering Activities
        activity?.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }


    @SuppressLint("CheckResult")
    private fun subscribeToTheMostRecentMovie() {
        viewModel?.getMostRecentMovie()
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ t ->
                    run {
                        handleMostRecentMovieDetails(t)
                    }
                }, { throwable ->
                    handleError(throwable)
                })
    }

    private fun handleMostRecentMovieDetails(t: MovieDTO) {
        //if movie does not have enough info, hide the space
        if (t.backdropPath == null){
            header.visibility = View.GONE
            return
        }

        t.backdropPath?.let { spotlight_movie_image?.loadPosterUrl(it, false) }
        spotlight_movie_description?.text = t.overview.formatMovieCardName(100)
        spotlight_movie_rating?.text = "%.1f".format(t.voteAverage)
        spotlight_movie_name?.text = t.title
    }

    @SuppressLint("CheckResult")
    private fun subscribeToTopRatedMovies() {
        viewModel?.findTopRatedMoviesList()
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ col ->
                    addNewDataToSecondAdapter()(ArrayList(col.results))
                }, { throwable ->
                    handleError(throwable)
                })
    }

    @SuppressLint("CheckResult")
    private fun subscribeToMostPopularMovies() {
        viewModel?.findMostPopularMovieList()
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ t: MovieCollectionDTO ->
                    addNewDataToFirstAdapter()(ArrayList(t.results))
                }, { t: Throwable? ->
                    handleError(t!!)
                })
    }


}
