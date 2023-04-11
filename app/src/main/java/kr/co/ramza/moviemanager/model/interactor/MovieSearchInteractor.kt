package kr.co.ramza.moviemanager.model.interactor

import kr.co.ramza.moviemanager.api.Result
import kr.co.ramza.moviemanager.api.ThemoviedbResult
import kr.co.ramza.moviemanager.api.ThemoviedbSearchService
import rx.Observable
import javax.inject.Inject

class MovieSearchInteractor @Inject constructor(val api : ThemoviedbSearchService){

    fun doSearch(query:String, page : Int = 1): Observable<ThemoviedbResult> {
        return api.search("ce7af642dcfca3e53aa12f927a5f9ed9", "ko-KR", query, page, "true")
    }
}