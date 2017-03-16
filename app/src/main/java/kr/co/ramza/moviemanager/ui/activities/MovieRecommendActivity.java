package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerActivityComponent;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.presenter.MovieRecommendPresenter;
import kr.co.ramza.moviemanager.ui.adapter.CategorySpinnerAdapter;
import kr.co.ramza.moviemanager.ui.adapter.RecommendMovieListAdapter;
import kr.co.ramza.moviemanager.ui.view.MovieRecommendView;

public class MovieRecommendActivity extends BaseActivity implements MovieRecommendView {

    @Inject
    MovieRecommendPresenter movieRecommendPresenter;

    @BindView(R.id.categorySpinner)
    Spinner categorySpinner;
    @BindView(R.id.haveSeenCheckBox)
    CheckBox haveSeenCheckBox;
    @BindView(R.id.searchTypeSpinner)
    Spinner searchTypeSpinner;
    @BindView(R.id.recommendBtn)
    Button recommendBtn;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Inject
    RecommendMovieListAdapter recommendMovieListAdapter;


    @Inject
    CategorySpinnerAdapter categorySpinnerAdapter;

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_movie_recommend;
    }

    @Override
    protected ActivityComponent getInitializeComponent() {
        return DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .build();
    }

    @Override
    protected void onInject(@Nullable ActivityComponent component) {
        if (component != null) {
            component.inject(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieRecommendPresenter.setView(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(recommendMovieListAdapter);

        categorySpinner.setAdapter(categorySpinnerAdapter);

        String[] searchType = {getString(R.string.mix),getString(R.string.sequence), getString(R.string.random)};
        searchTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, searchType));

        RxView.clicks(recommendBtn)
                .subscribe(event-> movieRecommendPresenter.startRecommend((Category) categorySpinner.getSelectedItem(),
                        haveSeenCheckBox.isChecked(), searchTypeSpinner.getSelectedItemPosition()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        recommendMovieListAdapter.checkValidList();
        recommendMovieListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRecommendMovie(int searchType, List<Movie> movieList) {
        recommendMovieListAdapter.setMovieRealmResults(searchType, movieList);
        recommendMovieListAdapter.notifyDataSetChanged();
    }

    public static Intent getIntent(Context context){
        return new Intent(context, MovieRecommendActivity.class);
    }
}
