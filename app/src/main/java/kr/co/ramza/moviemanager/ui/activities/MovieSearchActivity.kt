package kr.co.ramza.moviemanager.ui.activities

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_movie_search.*
import kr.co.ramza.moviemanager.R
import kr.co.ramza.moviemanager.api.SearchResponse
import kr.co.ramza.moviemanager.di.component.ActivityComponent
import kr.co.ramza.moviemanager.di.component.DaggerViewModelActivityComponent
import kr.co.ramza.moviemanager.di.component.ViewModelActivityComponent
import kr.co.ramza.moviemanager.di.module.ViewModelModule
import kr.co.ramza.moviemanager.ui.adapter.SearchMovieListAdapter
import kr.co.ramza.moviemanager.ui.view.MovieSearchViewModel
import kr.co.ramza.moviemanager.util.onScrollToEnd
import kr.co.ramza.moviemanager.util.toast
import javax.inject.Inject

class MovieSearchActivity : BaseActivity() {

    @Inject
    lateinit var movieSearchViewModel : MovieSearchViewModel

    lateinit var query : String

    lateinit var searchMovieListAdapter : SearchMovieListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        query = intent.getStringExtra(EXTRA_QUERY)

        searchMovieListAdapter = SearchMovieListAdapter(this)

        searchMovieRecyclerView.layoutManager = LinearLayoutManager(this)
        searchMovieRecyclerView.adapter = searchMovieListAdapter
        searchMovieRecyclerView.onScrollToEnd {
            movieSearchViewModel.search()
        }

        movieSearchViewModel.getSearchResult(query).observe(this, Observer {
            when(it){
                is SearchResponse.SUCCESS -> {
                    searchMovieListAdapter.addItems(it.items)
                }
                is SearchResponse.FAIL -> toast(it.reason)
            }
        })
    }

    override fun getContentViewResource() = R.layout.activity_movie_search

    override fun getInitializeComponent() = DaggerViewModelActivityComponent.builder()
            .applicationComponent(applicationComponent)
            .viewModelModule(ViewModelModule(this))
            .build()

    override fun onInject(component: ActivityComponent?) {
        when(component){
            is ViewModelActivityComponent ->
                component.inject(this)
        }
    }

    companion object {
        const val EXTRA_QUERY = "query"
    }
}
