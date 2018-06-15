package kr.co.ramza.moviemanager.ui.activities

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.LayoutInflater
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_movie_search.*
import kotlinx.android.synthetic.main.dialog_category_view.view.*
import kr.co.ramza.moviemanager.R
import kr.co.ramza.moviemanager.api.Item
import kr.co.ramza.moviemanager.api.SearchResponse
import kr.co.ramza.moviemanager.di.component.ActivityComponent
import kr.co.ramza.moviemanager.di.component.DaggerViewModelActivityComponent
import kr.co.ramza.moviemanager.di.component.ViewModelActivityComponent
import kr.co.ramza.moviemanager.di.module.ViewModelModule
import kr.co.ramza.moviemanager.model.Category
import kr.co.ramza.moviemanager.model.Movie
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor
import kr.co.ramza.moviemanager.ui.adapter.CategorySpinnerAdapter
import kr.co.ramza.moviemanager.ui.adapter.SearchMovieListAdapter
import kr.co.ramza.moviemanager.ui.view.MovieSearchViewModel
import kr.co.ramza.moviemanager.util.onScrollToEnd
import kr.co.ramza.moviemanager.util.toast
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class MovieSearchActivity : BaseActivity() {

    @Inject
    lateinit var movieSearchViewModel : MovieSearchViewModel

    @Inject
    lateinit var categorySpinnerAdapter: CategorySpinnerAdapter

    @Inject
    lateinit var realmInteractor: RealmInteractor

    lateinit var query : String

    lateinit var searchMovieListAdapter : SearchMovieListAdapter

    lateinit var progressDialog : Dialog

    private val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        query = intent.getStringExtra(EXTRA_QUERY)

        progressDialog = Dialog(this, R.style.ProgressDialog)
        progressDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        progressDialog.setContentView(R.layout.progressbar)
        progressDialog.setCancelable(false)

        searchMovieRecyclerView.apply {
            val layoutManager = LinearLayoutManager(this@MovieSearchActivity)
            this.layoutManager = layoutManager
            val dividerItemDecoration = DividerItemDecoration(searchMovieRecyclerView.context,
                    layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
        }.apply {
            searchMovieListAdapter = SearchMovieListAdapter(this@MovieSearchActivity,
                    ::onItemClick,
                    ::onItemLongClick)
            adapter = searchMovieListAdapter
        }.onScrollToEnd {
            movieSearchViewModel.search()
        }

        movieSearchViewModel.getSearchResult(query).observe(this, Observer {
            when(it){
                is SearchResponse.SUCCESS -> {
                    searchCountTextView.text = it.total.toString()
                    searchMovieListAdapter.addItems(it.items)
                }
                is SearchResponse.FAIL -> toast(it.reason)
            }
        })

        movieSearchViewModel.getLoadingState().observe(this, Observer {
            if(it!!){
                progressDialog.show()
            }else{
                progressDialog.dismiss()
            }
        })
    }

    fun onItemClick(item: Item){
        val intent = Intent(this, MovieSearchDetailActivity::class.java)
        intent.putExtra(MovieSearchDetailActivity.EXTRA_ITEM, item)
        startActivity(intent)
    }

    fun onItemLongClick(item: Item) : Boolean{
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_category_view, null)
        view.categorySpinner.adapter = categorySpinnerAdapter
        val title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(item.title, Html.FROM_HTML_MODE_COMPACT).toString()
        else
            Html.fromHtml(item.title).toString()
        subscriptions.add(dialog(this,
                "동영상 등록", "[ " + title + " ] 영화를 등록 하시겠습니까?",
                view
        ).filter {
            it == true
        }.subscribe {
            val category = view.categorySpinner.getSelectedItem() as Category
            realmInteractor.addMovie(Movie(category, title, null)).subscribe();
            toast(R.string.video_added)
        })
        return true
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

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.unsubscribe()
    }
}
