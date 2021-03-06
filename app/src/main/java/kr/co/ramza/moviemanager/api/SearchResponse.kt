package kr.co.ramza.moviemanager.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val title : String,
    val link : String,
    val image : String,
    val subtitle : String,
    val pubDate : String,
    val director : String?,
    val actor : String?,
    val userRating : String
) : Parcelable

data class Result(val total : Int, val start : Int, val display: Int, val items : MutableList<Item>)

sealed class SearchResponse{
    data class SUCCESS(val total : Int, val items : MutableList<Item>) : SearchResponse()
    data class FAIL(val reason : String) : SearchResponse()
}