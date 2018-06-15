package kr.co.ramza.moviemanager.ui.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import kr.co.ramza.moviemanager.api.NaverMovieSearchService
import kr.co.ramza.moviemanager.api.SearchResponse
import kr.co.ramza.moviemanager.model.interactor.MovieSearchInteractor
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription


class MovieSearchViewModel : ViewModel(){
    private var hasNext = true
    private val display = 100
    private var nextStart = 1
    private var query = ""
    private var searchResult = MutableLiveData<SearchResponse>()
    private var isLoading = MutableLiveData<Boolean>()

    private val subscriptions = CompositeSubscription()

    init {
        isLoading.value = false
    }

    val movieSearchInteractor = MovieSearchInteractor(NaverMovieSearchService.create())

    fun getSearchResult(query : String) : LiveData<SearchResponse>{
        this.query = query
        search()
        return searchResult
    }

    fun getLoadingState() = isLoading

    fun search(){
        if(hasNext && !isLoading.value!! && query.length > 0){
            subscriptions.add(movieSearchInteractor.doSearch(query, display, nextStart)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        isLoading.value = true
                    }
                    .doOnCompleted {
                        isLoading.value = false
                    }.subscribe({
                        result ->
                        hasNext = result.total > result.start + result.display
                        nextStart = result.start + result.display
                        searchResult.value = SearchResponse.SUCCESS(result.total, result.items)
                    }){ error ->
                        searchResult.value = SearchResponse.FAIL(error.message?:"Error")
                        isLoading.value = false
                    }
            )
        }
    }

    companion object {
        fun create(activity: AppCompatActivity) = ViewModelProviders.of(activity).get(MovieSearchViewModel::class.java)
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.unsubscribe()
    }
}