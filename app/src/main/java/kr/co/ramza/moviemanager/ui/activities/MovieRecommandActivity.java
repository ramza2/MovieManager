package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerActivityComponent;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.presenter.MovieRecommandPresenter;
import kr.co.ramza.moviemanager.ui.adapter.CategorySpinnerAdapter;
import kr.co.ramza.moviemanager.ui.view.MovieRecommandView;

public class MovieRecommandActivity extends BaseActivity implements MovieRecommandView{

    @Inject
    MovieRecommandPresenter movieRecommandPresenter;

    @BindView(R.id.categorySpinner)
    Spinner categorySpinner;
    @BindView(R.id.haveSeenCheckBox)
    CheckBox haveSeenCheckBox;
    @BindView(R.id.searchTypeSpinner)
    Spinner searchTypeSpinner;
    @BindView(R.id.recommandBtn)
    Button recommandBtn;
    @BindView(R.id.movieRecommandLayout)
    LinearLayout movieRecommandLayout;
    @BindView(R.id.movieNameTextView)
    TextView movieNameTextView;
    @BindView(R.id.categorNameTextView)
    TextView categorNameTextView;

    @Inject
    CategorySpinnerAdapter categorySpinnerAdapter;

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_movie_recommand;
    }

    @Override
    protected ActivityComponent getInitializeCompoent() {
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

        movieRecommandPresenter.setView(this);

        categorySpinner.setAdapter(categorySpinnerAdapter);

        String[] searchType = {getString(R.string.mix),getString(R.string.sequence), getString(R.string.random)};
        searchTypeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, searchType));

        RxView.clicks(recommandBtn)
                .subscribe(event->movieRecommandPresenter.startRecommand((Category) categorySpinner.getSelectedItem(),
                        haveSeenCheckBox.isChecked(), searchTypeSpinner.getSelectedItemPosition()));

        RxView.clicks(movieRecommandLayout)
                .subscribe(event->{
                    startActivity(MovieDetailActivity.getIntent(MovieRecommandActivity.this, (Long) movieRecommandLayout.getTag()));
                    finish();
                });
    }

    @Override
    public void showRecommandMovie(Movie movie) {
        movieRecommandLayout.setTag(movie.getId());
        movieNameTextView.setText(movie.getName());
        Category category = movie.getCategory();
        categorNameTextView.setText(category != null ? category.getName() : null);
    }

    public static Intent getIntent(Context context){
        return new Intent(context, MovieRecommandActivity.class);
    }
}
