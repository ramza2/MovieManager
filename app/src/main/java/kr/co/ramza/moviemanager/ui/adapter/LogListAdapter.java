package kr.co.ramza.moviemanager.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.ui.activities.MovieDetailActivity;

/**
 * Created by 전창현 on 2017-02-28.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.ViewHolder>{
    private RealmResults<Log> logRealmResults;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = (long) v.getTag();
                if(id > 0){
                    Context context = v.getContext();
                    context.startActivity(MovieDetailActivity.getIntent(context, id));
                }
            }
        });
        holder.numTextView.setText((position + 1) + ".");
        Category category = null;
        if(movie != null){
            category = movie.getCategory();
        }
        holder.movieNameTextView.setText(movie != null ? movie.getName() : null);
        holder.categorNameTextView.setText(category != null ? category.getName() : null);
    }

    @Override
    public int getItemCount() {
        return logRealmResults != null ? logRealmResults.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.numTextView)
        TextView numTextView;
        @BindView(R.id.movieNameTextView)
        TextView movieNameTextView;
        @BindView(R.id.categorNameTextView)
        TextView categorNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
