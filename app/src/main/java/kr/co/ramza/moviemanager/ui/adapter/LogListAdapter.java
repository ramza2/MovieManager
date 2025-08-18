package kr.co.ramza.moviemanager.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import io.realm.RealmResults;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.databinding.ItemLogListBinding;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.ui.activities.MovieDetailActivity;
import kr.co.ramza.moviemanager.ui.helper.ItemTouchHelperAdapter;

/**
 * Created by 전창현 on 2017-02-28.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private RealmResults<Log> logRealmResults;

    private RealmInteractor realmInteractor;

    @Inject
    public LogListAdapter(RealmInteractor realmInteractor) {
        this.realmInteractor = realmInteractor;
    }

    public void setLogRealmResults(RealmResults<Log> logRealmResults) {
        this.logRealmResults = logRealmResults;
    }

    @Override
    public LogListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log log  = logRealmResults.get(position);
        Movie movie = log.getMovie();
        holder.itemView.setTag(movie != null ? movie.getId() : 0);
        holder.itemView.setOnClickListener(v -> {
            long id = (long) v.getTag();
            if(id > 0){
                Context context = v.getContext();
                context.startActivity(MovieDetailActivity.getIntent(context, id));
            }
        });
        holder.binding.numTextView.setText((position + 1) + ".");
        Category category = null;
        if(movie != null){
            category = movie.getCategory();
        }
        holder.binding.movieNameTextView.setText(movie != null ? movie.getName() : null);
        holder.binding.categoryNameTextView.setText(category != null ? category.getName() : null);
    }

    @Override
    public int getItemCount() {
        return logRealmResults != null ? logRealmResults.size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        Log log = logRealmResults.get(position);
        realmInteractor.deleteLog(log);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final ItemLogListBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ItemLogListBinding.bind(itemView);
        }
    }
}
