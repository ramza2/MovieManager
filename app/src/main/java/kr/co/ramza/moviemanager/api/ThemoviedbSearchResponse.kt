package kr.co.ramza.moviemanager.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ThemoviedbItem(
    val title : String,
    val poster_path : String,
    val overview : String,
    val release_date : String,
    val vote_average : Float
) : Parcelable

data class ThemoviedbResult(val page : Int, val total_pages : Int, val total_results: Int, val results : MutableList<ThemoviedbItem>)

sealed class ThemoviedbSearchResponse{
    data class SUCCESS(val total : Int, val items : MutableList<ThemoviedbItem>) : ThemoviedbSearchResponse()
    data class FAIL(val reason : String) : ThemoviedbSearchResponse()
}