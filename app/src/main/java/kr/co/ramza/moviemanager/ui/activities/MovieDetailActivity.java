package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.databinding.ActivityMovieDetailBinding;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerPresenterActivityComponent;
import kr.co.ramza.moviemanager.di.component.PresenterActivityComponent;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.presenter.MovieDetailPresenter;
import kr.co.ramza.moviemanager.ui.adapter.CategorySpinnerAdapter;
import kr.co.ramza.moviemanager.ui.view.ClearEditText;
import kr.co.ramza.moviemanager.ui.view.MovieDetailView;
import rx.subscriptions.CompositeSubscription;

public class MovieDetailActivity extends BaseActivity implements MovieDetailView{

    public static final String EXTRA_ID = "id";

    @Inject
    MovieDetailPresenter movieDetailPresenter;

    private ActivityMovieDetailBinding binding;

    @Inject
    CategorySpinnerAdapter categorySpinnerAdapter;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_movie_detail;
    }

    @Override
    protected ActivityComponent getInitializeComponent() {
        return DaggerPresenterActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .build();
    }

    @Override
    protected void onInject(@Nullable ActivityComponent component) {
        if (component != null) {
            ((PresenterActivityComponent)component).inject(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieDetailPresenter.setView(this);

        binding.categorySpinner.setAdapter(categorySpinnerAdapter);

        subscriptions.add(RxView.clicks(binding.modifyBtn)
                .subscribe(event->{
                    movieDetailPresenter.modifyMovie(binding.movieNameEditText.getText().toString().trim(), binding.seriesEditText.getText().toString().trim(),
                            (Category) binding.categorySpinner.getSelectedItem(), binding.haveSeenCheckBox.isChecked(), binding.starNumRatingBar.getRating());
                    finish();
                }));

        subscriptions.add(RxView.clicks(binding.revertBtn)
                .subscribe(event->{
                    movieDetailPresenter.loadOrgMovie();
                }));

        subscriptions.add(RxView.clicks(binding.deleteBtn)
                .subscribe(event->{
                    movieDetailPresenter.deleteMovie();
                    finish();
                }));

        long id = getIntent().getLongExtra(EXTRA_ID, 0);
        movieDetailPresenter.loadMovie(id);
    }

    @Override
    public void showMovieInfo(Movie movie) {
        binding.movieNameEditText.setText(movie.getName());
        binding.seriesEditText.setText(movie.getSeries());

        Category category = movie.getCategory();
        ArrayList<Category> categories = categorySpinnerAdapter.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            if(category.getId() == categories.get(i).getId()){
                binding.categorySpinner.setSelection(i);
                break;
            }
        }

        binding.haveSeenCheckBox.setChecked(movie.isHaveSeen());

        binding.starNumRatingBar.setRating(movie.getStarNum());
    }

    public static Intent getIntent(Context context,@IntRange(from = 1) long id){
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        subscriptions.unsubscribe();
    }
}
