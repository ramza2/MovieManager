package kr.co.ramza.moviemanager.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.ramza.moviemanager.R
import kr.co.ramza.moviemanager.api.ThemoviedbItem
import kr.co.ramza.moviemanager.api.ThemoviedbSearchResponse
import kr.co.ramza.moviemanager.databinding.ActivityMovieSearchBinding
import kr.co.ramza.moviemanager.databinding.DialogCategoryViewBinding
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

    private var binding: ActivityMovieSearchBinding? = null

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
        
        binding = ActivityMovieSearchBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        query = intent.getStringExtra(EXTRA_QUERY).toString()

        progressDialog = Dialog(this, R.style.ProgressDialog)
        progressDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        progressDialog.setContentView(R.layout.progressbar)
        progressDialog.setCancelable(false)

        binding!!.searchMovieRecyclerView.apply {
            val layoutManager =
                LinearLayoutManager(this@MovieSearchActivity)
            this.layoutManager = layoutManager
            val dividerItemDecoration =
                DividerItemDecoration(
                    binding!!.searchMovieRecyclerView.context,
                    layoutManager.orientation
                )
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
                is ThemoviedbSearchResponse.SUCCESS -> {
                    binding!!.searchCountTextView.text = it.total.toString()
                    searchMovieListAdapter.addItems(it.items)
                }
                is ThemoviedbSearchResponse.FAIL -> toast(it.reason)
                else -> throw AssertionError()
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

    fun onItemClick(item: ThemoviedbItem){
        val intent = Intent(this, MovieSearchDetailActivity::class.java)
        intent.putExtra(MovieSearchDetailActivity.EXTRA_ITEM, item)
        startActivity(intent)
    }

    fun onItemLongClick(item: ThemoviedbItem) : Boolean{
        val dialogBinding = DialogCategoryViewBinding.inflate(LayoutInflater.from(this))
        dialogBinding.categorySpinner.adapter = categorySpinnerAdapter
        val title = item.title
        subscriptions.add(dialog(this,
                "동영상 등록", "[ " + title + " ] 영화를 등록 하시겠습니까?",
                dialogBinding.root
        ).filter {
            it == true
        }.subscribe {
            val category = dialogBinding.categorySpinner.selectedItem as Category
            if(category != null){
                realmInteractor.addMovie(Movie(category, title, null)).subscribe();
                toast(R.string.video_added)
            }else{
                toast(R.string.please_select_category)
            }
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
        if (subscriptions != null && !subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe()
        }
        binding = null
    }
}
