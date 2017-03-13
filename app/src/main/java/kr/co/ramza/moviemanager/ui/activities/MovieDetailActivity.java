package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ramza.moviemanager.MovieManagerApplication;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.presenter.impl.MovieDetailPresenterImpl;
import kr.co.ramza.moviemanager.ui.adapter.CategorySpinnerAdapter;
import kr.co.ramza.moviemanager.ui.view.MovieDetailView;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailView{

    public static final String EXTRA_ID = "id";

    @Inject
    MovieDetailPresenterImpl movieDetailPresenter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        ((MovieManagerApplication)getApplicationContext()).getApplicationComponent().inject(this);

        movieDetailPresenter.setView(this);

        categorySpinner.setAdapter(categorySpinnerAdapter);

        RxView.clicks(modifyBtn)
                .subscribe(event->{
                    movieDetailPresenter.modifyMovie(movieNameEditText.getText().toString(),
                            (Category) categorySpinner.getSelectedItem(), haveSeenCheckBox.isChecked(), starNumRatingBar.getRating());
                    finish();
                });

        RxView.clicks(deleteBtn)
                .subscribe(event->{
                    movieDetailPresenter.deleteMovie();
                    finish();
                });

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

    public static Intent getIntent(Context context, long id){
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }
}
