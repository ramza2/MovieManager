package kr.co.ramza.moviemanager.model.interactor

import kr.co.ramza.moviemanager.api.NaverMovieSearchService
import kr.co.ramza.moviemanager.api.Result
import rx.Observable
import javax.inject.Inject

class MovieSearchInteractor @Inject constructor(val api : NaverMovieSearchService){

    fun doSearch(query:String, display : Int = 100, start : Int = 1): Observable<Result> {
        return api.search(query, display, start)
    }
}