package kr.co.ramza.moviemanager.util

import android.content.Context
import android.widget.Toast

fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


fun Context.toast(resId: Int) =
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()