package kr.co.ramza.moviemanager.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
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
        if(!item.image.isNullOrEmpty()){
            holder.movieImage.run {
                visibleOrGone = true
                GlideApp.with(holder.itemView).load(item.image).into(this)
            }
        }else{
            holder.movieImage.visibleOrGone = false
        }
        holder.titleTextView.text = item.title
        if(!item.subtitle.isNullOrEmpty()){
            holder.subtitleTextView.run {
                visibleOrGone = true
                text = item.subtitle
            }
        }else{
            holder.subtitleTextView.visibleOrGone = false
        }
        holder.directorTextView.text = item.director
        holder.pubDateTextView.text = item.pubDate

    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val movieImage = view.movieImage
        val titleTextView = view.titleTextView
        val subtitleTextView = view.subtitleTextView
        val directorTextView = view.directorTextView
        val pubDateTextView = view.pubDateTextView
    }
}