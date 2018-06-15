package kr.co.ramza.moviemanager.util

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.*

var View.visibleOrGone
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }

var View.visible
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else INVISIBLE
    }

var View.invisible
    get() = visibility == INVISIBLE
    set(value) {
        visibility = if (value) INVISIBLE else VISIBLE
    }

var View.gone
    get() = visibility == GONE
    set(value) {
        visibility = if (value) GONE else VISIBLE
    }

fun RecyclerView.onScrollToEnd(onScrollNearEnd: (Unit) -> Unit)
        = addOnScrollListener(object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        val linearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager
        if (linearLayoutManager.childCount + linearLayoutManager.findFirstVisibleItemPosition()
                >= linearLayoutManager.itemCount) {  //if near fifth item from end
            onScrollNearEnd(Unit)
        }
    }
})