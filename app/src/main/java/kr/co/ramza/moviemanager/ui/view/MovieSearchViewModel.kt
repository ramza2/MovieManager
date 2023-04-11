package kr.co.ramza.moviemanager.ui.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import kr.co.ramza.moviemanager.api.ThemoviedbSearchResponse
import kr.co.ramza.moviemanager.api.ThemoviedbSearchService
import kr.co.ramza.moviemanager.model.interactor.MovieSearchInteractor
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription


class MovieSearchViewModel : ViewModel(){
    private var hasNext = true
    private var page = 1
    private var query = ""
    private var searchResult = MutableLiveData<ThemoviedbSearchResponse>()
    private var isLoading = MutableLiveData<Boolean>()

    private val subscriptions = CompositeSubscription()

    init {
        isLoading.value = false
    }

    val movieSearchInteractor = MovieSearchInteractor(ThemoviedbSearchService.create())

    fun getSearchResult(query : String) : LiveData<ThemoviedbSearchResponse>{
        this.query = query
        search()
        return searchResult
    }

    fun getLoadingState() = isLoading

    fun search(){
        if(hasNext && !isLoading.value!! && query.length > 0){
            subscriptions.add(movieSearchInteractor.doSearch(query, page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        isLoading.value = true
                    }
                    .doOnCompleted {
                        isLoading.value = false
                    }.subscribe({
                        result ->
                        hasNext = result.total_pages > result.page
                        page++
                        searchResult.value = ThemoviedbSearchResponse.SUCCESS(result.total_results, result.results)
                    }){ error ->
                        searchResult.value = ThemoviedbSearchResponse.FAIL(error.message?:"Error")
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