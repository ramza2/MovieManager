package kr.co.ramza.moviemanager.ui.adapter

import android.content.Context
import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.ramza.moviemanager.R
import kr.co.ramza.moviemanager.api.Item
import kr.co.ramza.moviemanager.api.ThemoviedbItem
import kr.co.ramza.moviemanager.databinding.ItemSearchMovieListBinding
import kr.co.ramza.moviemanager.util.visibleOrGone

class SearchMovieListAdapter(val context : Context, val clickListener: ((ThemoviedbItem)->Unit)?, val longClickListener : ((ThemoviedbItem)->Boolean)? ) : RecyclerView.Adapter<SearchMovieListAdapter.ViewHolder>() {

    private val items = mutableListOf<ThemoviedbItem>()

    fun addItems(items:List<ThemoviedbItem>){
        val start = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(start, items.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchMovieListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.run {
            numTextView.text = (position+1).toString() + "."

            titleTextView.text = item.title
            if(!item.overview.isNullOrEmpty()){
                subtitleTextView.run {
                    isSelected = true
                    visibleOrGone = true
                    text = item.overview
                }
            }else{
                subtitleTextView.visibleOrGone = false
            }
            pubDateTextView.text = item.release_date
            userRatingTextView.text = item.vote_average.toString()

            root.setOnClickListener {
                clickListener?.invoke(item)
            }
            root.setOnLongClickListener {
                longClickListener?.invoke(item)?:false
            }
        }

    }

    class ViewHolder(val binding: ItemSearchMovieListBinding) : RecyclerView.ViewHolder(binding.root)
}