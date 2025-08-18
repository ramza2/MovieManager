package kr.co.ramza.moviemanager.api

import android.os.Parcel
import android.os.Parcelable

data class Item(
    val title : String,
    val link : String,
    val image : String,
    val subtitle : String,
    val pubDate : String,
    val director : String?,
    val actor : String?,
    val userRating : String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(link)
        parcel.writeString(image)
        parcel.writeString(subtitle)
        parcel.writeString(pubDate)
        parcel.writeString(director)
        parcel.writeString(actor)
        parcel.writeString(userRating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}

data class Result(val total : Int, val start : Int, val display: Int, val items : MutableList<Item>)

sealed class SearchResponse{
    data class SUCCESS(val total : Int, val items : MutableList<Item>) : SearchResponse()
    data class FAIL(val reason : String) : SearchResponse()
}