package kr.co.ramza.moviemanager.api

data class Item(
    val title : String,
    val link : String,
    val image : String,
    val subtitle : String,
    val pubDate : String,
    val director : String,
    val actor : String,
    val userRating : String
)

data class Result(val total : Int, val start : Int, val display: Int, val items : MutableList<Item>)

sealed class SearchResponse{
    data class SUCCESS(val items : MutableList<Item>) : SearchResponse()
    data class FAIL(val reason : String) : SearchResponse()
}