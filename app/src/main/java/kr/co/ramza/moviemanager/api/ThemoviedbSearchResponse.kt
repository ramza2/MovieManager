package kr.co.ramza.moviemanager.api

import android.os.Parcel
import android.os.Parcelable

data class ThemoviedbItem(
    val title : String,
    val poster_path : String,
    val overview : String,
    val release_date : String,
    val vote_average : Float
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(poster_path)
        parcel.writeString(overview)
        parcel.writeString(release_date)
        parcel.writeFloat(vote_average)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ThemoviedbItem> {
        override fun createFromParcel(parcel: Parcel): ThemoviedbItem {
            return ThemoviedbItem(parcel)
        }

        override fun newArray(size: Int): Array<ThemoviedbItem?> {
            return arrayOfNulls(size)
        }
    }
}

data class ThemoviedbResult(val page : Int, val total_pages : Int, val total_results: Int, val results : MutableList<ThemoviedbItem>)

sealed class ThemoviedbSearchResponse{
    data class SUCCESS(val total : Int, val items : MutableList<ThemoviedbItem>) : ThemoviedbSearchResponse()
    data class FAIL(val reason : String) : ThemoviedbSearchResponse()
}