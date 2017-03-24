package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerActivityComponent;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.presenter.MovieDetailPresenter;
import kr.co.ramza.moviemanager.ui.adapter.CategorySpinnerAdapter;
import kr.co.ramza.moviemanager.ui.view.MovieDetailView;
import rx.subscriptions.CompositeSubscription;

public class MovieDetailActivity extends BaseActivity implements MovieDetailView{

    public static final String EXTRA_ID = "id";

    @Inject
    MovieDetailPresenter movieDetailPresenter;

    @BindView(R.id.categorySpinner)
    Spinner categorySpinner;

    @BindView(R.id.movieNameEditText)
    EditText movieNameEditText;

    @BindView(R.id.haveSeenCheckBox)
    CheckBox haveSeenCheckBox;
    @BindView(R.id.starNumRatingBar)
    RatingBar starNumRatingBar;
    @BindView(R.id.modifyBtn)
    Button modifyBtn;

    @BindView(R.id.deleteBtn)
    Button deleteBtn;

    @Inject
    CategorySpinnerAdapter categorySpinnerAdapter;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_movie_detail;
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

        movieDetailPresenter.setView(this);

        categorySpinner.setAdapter(categorySpinnerAdapter);

        subscriptions.add(RxView.clicks(modifyBtn)
                .subscribe(event->{
                    movieDetailPresenter.modifyMovie(movieNameEditText.getText().toString(),
                            (Category) categorySpinner.getSelectedItem(), haveSeenCheckBox.isChecked(), starNumRatingBar.getRating());
                    finish();
                }));

        subscriptions.add(RxView.clicks(deleteBtn)
                .subscribe(event->{
                    movieDetailPresenter.deleteMovie();
                    finish();
                }));

        long id = getIntent().getLongExtra(EXTRA_ID, 0);
        movieDetailPresenter.loadMovie(id);
    }

    @Override
    public void showMovieInfo(Movie movie) {
        movieNameEditText.setText(movie.getName());

        Category category = movie.getCategory();
        ArrayList<Category> categories = categorySpinnerAdapter.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            if(category.getId() == categories.get(i).getId()){
                categorySpinner.setSelection(i);
                break;
            }
        }

        haveSeenCheckBox.setChecked(movie.isHaveSeen());

        starNumRatingBar.setRating(movie.getStarNum());
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
