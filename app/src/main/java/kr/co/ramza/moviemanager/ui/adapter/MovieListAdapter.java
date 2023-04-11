package kr.co.ramza.moviemanager.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.ui.activities.MovieDetailActivity;
import kr.co.ramza.moviemanager.ui.helper.ItemTouchHelperAdapter;

/**
 * Created by 전창현 on 2017-02-28.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private RealmResults<Movie> movieRealmResults;

    private RealmInteractor realmInteractor;

    @Inject
    public MovieListAdapter(RealmInteractor realmInteractor) {
        this.realmInteractor = realmInteractor;
    }

    public void setMovieRealmResults(RealmResults<Movie> movieRealmResults) {
        this.movieRealmResults = movieRealmResults;
    }

    @Override
    public MovieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movieRealmResults.get(position);
        holder.itemView.setTag(movie.getId());
        holder.itemView.setOnClickListener(v -> {
            long id = (long) v.getTag();
            Context context = v.getContext();
            context.startActivity(MovieDetailActivity.getIntent(context, id));
        });
        holder.numTextView.setText((position + 1) + ".");
        holder.movieNameTextView.setText(movie.getName());
        Category category = movie.getCategory();
        holder.categoryNameTextView.setText(category != null ? category.getName() : null);
        holder.haveSeenTextView.setText(movie.isHaveSeen() ?
                holder.itemView.getContext().getString(R.string.have_seen) : holder.itemView.getContext().getString(R.string.not_have_seen) );
        holder.starNumTextView.setText(String.valueOf(movie.getStarNum()));
    }

    @Override
    public int getItemCount() {
        return movieRealmResults != null ? movieRealmResults.size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        Movie movie = movieRealmResults.get(position);
        realmInteractor.deleteMovie(movie);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.numTextView)
        TextView numTextView;
        @BindView(R.id.movieNameTextView)
        TextView movieNameTextView;
        @BindView(R.id.haveSeenTextView)
        TextView haveSeenTextView;
        @BindView(R.id.starNumTextView)
        TextView starNumTextView;
        @BindView(R.id.categoryNameTextView)
        TextView categoryNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
