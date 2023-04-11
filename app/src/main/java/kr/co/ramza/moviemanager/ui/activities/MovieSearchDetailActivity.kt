package kr.co.ramza.moviemanager.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_movie_search_detail.*
import kotlinx.android.synthetic.main.dialog_category_view.view.*
import kr.co.ramza.moviemanager.R
import kr.co.ramza.moviemanager.api.ThemoviedbItem
import kr.co.ramza.moviemanager.di.component.ActivityComponent
import kr.co.ramza.moviemanager.di.component.DaggerActivityComponent
import kr.co.ramza.moviemanager.di.module.GlideApp
import kr.co.ramza.moviemanager.model.Category
import kr.co.ramza.moviemanager.model.Movie
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor
import kr.co.ramza.moviemanager.ui.adapter.CategorySpinnerAdapter
import kr.co.ramza.moviemanager.util.gone
import kr.co.ramza.moviemanager.util.toast
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class MovieSearchDetailActivity : BaseActivity() {

    lateinit var item : ThemoviedbItem

    @Inject
    lateinit var categorySpinnerAdapter: CategorySpinnerAdapter

    @Inject
    lateinit var realmInteractor: RealmInteractor

    private val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        item = intent.getParcelableExtra(EXTRA_ITEM)!!

        with(item){
            titleTextView.text = original_title
            subtitleTextView.text = overview
            pubDateTextView.text = release_date
            userRatingTextView.text = vote_average.toString()
            if(!poster_path.isNullOrEmpty()){
                GlideApp.with(this@MovieSearchDetailActivity)
                        .load("https://image.tmdb.org/t/p/original" + poster_path)
                        .into(movieImageView)
            }else{
                movieImageView.gone = true
            }
        }

        addMovieBtn.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_category_view, null)
            view.categorySpinner.adapter = categorySpinnerAdapter
            val title = item.original_title
            subscriptions.add(dialog(this,
                    "동영상 등록", "[ " + title + " ] 영화를 등록 하시겠습니까?",
                    view
            ).filter {
                it == true
            }.subscribe {
                val category = view.categorySpinner.selectedItem as Category
                if(category != null){
                    realmInteractor.addMovie(Movie(category, title, null)).subscribe();
                    toast(R.string.video_added)
                }else{
                    toast(R.string.please_select_category)
                }
            })
        }
    }

    override fun getContentViewResource() = R.layout.activity_movie_search_detail

    override fun getInitializeComponent()= DaggerActivityComponent.builder()
            .applicationComponent(applicationComponent)
            .build()

    override fun onInject(component: ActivityComponent?) {
        when(component){
            is DaggerActivityComponent ->
                component.inject(this)
        }
    }

    companion object {
        const val EXTRA_ITEM = "item"
    }
}
