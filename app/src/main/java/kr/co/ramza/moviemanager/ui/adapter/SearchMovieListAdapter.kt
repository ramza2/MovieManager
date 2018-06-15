package kr.co.ramza.moviemanager.ui.adapter

import android.content.Context
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_search_movie_list.view.*
import kr.co.ramza.moviemanager.R
import kr.co.ramza.moviemanager.api.Item
import kr.co.ramza.moviemanager.di.module.GlideApp
import kr.co.ramza.moviemanager.util.visibleOrGone

class SearchMovieListAdapter(val context : Context) : RecyclerView.Adapter<SearchMovieListAdapter.ViewHolder>() {

    private val items = mutableListOf<Item>()

    fun addItems(items:List<Item>){
        val start = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(start, items.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_movie_list, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.run {
            numTextView.text = (position+1).toString() + "."
            if(!item.image.isNullOrEmpty()){
                movieImage.run {
                    visibleOrGone = true
                    GlideApp.with(holder.itemView).load(item.image).into(this)
                }
            }else{
                movieImage.visibleOrGone = false
            }

            titleTextView.text =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(item.title, Html.FROM_HTML_MODE_COMPACT)
                    else
                        Html.fromHtml(item.title)
            titleTextView.isSelected = true
            if(!item.subtitle.isNullOrEmpty()){
                subtitleTextView.run {
                    isSelected = true
                    visibleOrGone = true
                    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(Html.fromHtml(item.subtitle, Html.FROM_HTML_MODE_LEGACY).toString(),Html.FROM_HTML_MODE_LEGACY)
                    else
                        Html.fromHtml(Html.fromHtml(item.subtitle).toString())
                }
            }else{
                subtitleTextView.visibleOrGone = false
            }
            pubDateTextView.text = item.pubDate
        }

    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var numTextView = view.numTextView
        val movieImage = view.movieImage
        val titleTextView = view.titleTextView
        val subtitleTextView = view.subtitleTextView
        val pubDateTextView = view.pubDateTextView
    }
}