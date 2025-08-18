package kr.co.ramza.moviemanager.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import kr.co.ramza.moviemanager.R
import kr.co.ramza.moviemanager.api.ThemoviedbItem
import kr.co.ramza.moviemanager.databinding.ActivityMovieSearchDetailBinding
import kr.co.ramza.moviemanager.databinding.DialogCategoryViewBinding
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

    private var binding: ActivityMovieSearchDetailBinding? = null
    lateinit var item : ThemoviedbItem

    @Inject
    lateinit var categorySpinnerAdapter: CategorySpinnerAdapter

    @Inject
    lateinit var realmInteractor: RealmInteractor

    private val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSearchDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        item = intent.getParcelableExtra(EXTRA_ITEM)!!

        with(item){
            binding!!.titleTextView.text = title
            binding!!.subtitleTextView.text = overview
            binding!!.pubDateTextView.text = release_date
            binding!!.userRatingTextView.text = vote_average.toString()
            if(!poster_path.isNullOrEmpty()){
                GlideApp.with(this@MovieSearchDetailActivity)
                        .load("https://image.tmdb.org/t/p/original" + poster_path)
                        .into(binding!!.movieImageView)
            }else{
                binding!!.movieImageView.gone = true
            }
        }

        binding!!.addMovieBtn.setOnClickListener {
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
        }
    }

    override fun getContentViewResource() = R.layout.activity_movie_search_detail

    override fun getInitializeComponent()= DaggerActivityComponent.builder()
            .applicationComponent(applicationComponent)
            .build()

    override fun onInject(component: ActivityComponent?) {
        component?.inject(this)
    }

    companion object {
        const val EXTRA_ITEM = "item"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subscriptions != null && !subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe()
        }
        binding = null
    }
}
