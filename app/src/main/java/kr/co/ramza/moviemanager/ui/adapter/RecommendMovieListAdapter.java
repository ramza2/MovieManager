package kr.co.ramza.moviemanager.ui.adapter;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.databinding.ItemRecommendMovieListBinding;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.ui.activities.MovieDetailActivity;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by 전창현 on 2017-02-28.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class RecommendMovieListAdapter extends RecyclerView.Adapter<RecommendMovieListAdapter.ViewHolder>{
    private List<Movie> movieList;
    private int searchType;

    private RealmInteractor realmInteractor;

    @Inject
    public RecommendMovieListAdapter(RealmInteractor realmInteractor) {
        this.realmInteractor = realmInteractor;
    }

    public void setMovieRealmResults(int searchType, List<Movie> movieList) {
        this.searchType = searchType;
        this.movieList = movieList;
    }

    @Override
    public RecommendMovieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_movie_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie  = movieList.get(position);
        holder.itemView.setTag(movie);
        holder.itemView.setOnClickListener(v -> {
            Movie movieTag = (Movie) v.getTag();
            dialog(v.getContext(), R.string.add_log, R.string.question_add_log)
                    .subscribe(sel -> {
                        switch (sel) {
                            case 0 :
                                Log newLog = new Log();
                                newLog.setMovie(movieTag);
                                newLog.setSearchType(searchType);
                                realmInteractor.addLog(newLog)
                                        .doOnNext(log -> Toast.makeText(v.getContext(), R.string.add_log_success, Toast.LENGTH_SHORT).show())
                                        .doOnError(throwable -> Toast.makeText(v.getContext(), R.string.add_log_fail, Toast.LENGTH_SHORT).show())
                                        .subscribe();
                                break;
                            case 1:
                                Context context = v.getContext();
                                context.startActivity(MovieDetailActivity.getIntent(context, movieTag.getId()));
                                break;
                        }

                    });
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
        return movieList != null ? movieList.size() : 0;
    }

    Observable<Integer> dialog(Context context, @StringRes int title,@StringRes int message) {
        return Observable.create((Subscriber<? super Integer> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        subscriber.onNext(0);
                        subscriber.onCompleted();
                    })
                    .setNeutralButton(R.string.detail_view, (dialog, which) -> {
                        subscriber.onNext(1);
                        subscriber.onCompleted();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        subscriber.onNext(2);
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }

    public void checkValidList() {
        if(movieList != null){
            for (int i = movieList.size() - 1; i >= 0; i--) {
                Movie movie = movieList.get(i);
                if(!movie.isValid()){
                    movieList.remove(movie);
                }
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final ItemRecommendMovieListBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ItemRecommendMovieListBinding.bind(itemView);
        }
    }
}
