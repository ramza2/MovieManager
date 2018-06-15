package kr.co.ramza.moviemanager.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_movie_search_detail.*
import kotlinx.android.synthetic.main.dialog_category_view.view.*
import kr.co.ramza.moviemanager.R
import kr.co.ramza.moviemanager.api.Item
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

    lateinit var item : Item

    @Inject
    lateinit var categorySpinnerAdapter: CategorySpinnerAdapter

    @Inject
    lateinit var realmInteractor: RealmInteractor

    private val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        item = intent.getParcelableExtra(EXTRA_ITEM)

        with(item){
            titleTextView.text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY )
            else
                Html.fromHtml(title)
            subtitleTextView.text =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(Html.fromHtml(subtitle, Html.FROM_HTML_MODE_LEGACY).toString(),Html.FROM_HTML_MODE_LEGACY)
                    else
                        Html.fromHtml(Html.fromHtml(subtitle).toString())
            pubDateTextView.text = pubDate
            userRatingTextView.text = userRating
            var lastIndex : Int = director?.lastIndexOf("|") ?: -1
            directorTextView.text = (if(lastIndex > 0) director?.substring(0, lastIndex) else director)?.replace("|", ",")
            lastIndex = actor?.lastIndexOf("|") ?: -1
            actorTextView.text = (if(lastIndex > 0) actor?.substring(0, lastIndex) else actor)?.replace("|", ",")

            if(!image.isNullOrEmpty()){
                GlideApp.with(this@MovieSearchDetailActivity)
                        .load(image)
                        .into(movieImageView)
            }else{
                movieImageView.gone = true
            }
        }

        addMovieBtn.setOnClickListener {
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
