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
import kr.co.ramza.moviemanager.util.visibleOrGone

class SearchMovieListAdapter(val context : Context, val clickListener: ((Item)->Unit)?, val longClickListener : ((Item)->Boolean)? ) : RecyclerView.Adapter<SearchMovieListAdapter.ViewHolder>() {

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

            titleTextView.text =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(item.title, Html.FROM_HTML_MODE_LEGACY )
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
            userRatingTextView.text = item.userRating

            itemView.setOnClickListener {
                clickListener?.invoke(item)
            }
            itemView.setOnLongClickListener {
                longClickListener?.invoke(item)?:false
            }
        }

    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var numTextView = view.numTextView
        val userRatingTextView = view.userRatingTextView
        val titleTextView = view.titleTextView
        val subtitleTextView = view.subtitleTextView
        val pubDateTextView = view.pubDateTextView
    }
}