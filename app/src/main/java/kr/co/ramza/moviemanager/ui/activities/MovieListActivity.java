package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.RealmResults;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerActivityComponent;
import kr.co.ramza.moviemanager.di.module.ActivityModule;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.presenter.MovieListPresenter;
import kr.co.ramza.moviemanager.ui.adapter.CategorySpinnerAdapter;
import kr.co.ramza.moviemanager.ui.adapter.MovieListAdapter;
import kr.co.ramza.moviemanager.ui.helper.SimpleItemTouchHelperCallback;
import kr.co.ramza.moviemanager.ui.view.MovieListView;

public class MovieListActivity extends BaseActivity implements MovieListView{

    @BindView(R.id.addMovieBtn)
    Button addMovieBtn;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.categorySpinner)
    Spinner categorySpinner;

    @BindView(R.id.haveSeenSpinner)
    Spinner haveSeenSpinner;

    @BindView(R.id.movieNameEditText)
    EditText movieNameEditText;

    @BindView(R.id.searchCountTextView)
    TextView searchCountTextView;

    @Inject
    MovieListPresenter movieListPresenter;

    @Inject
    MovieListAdapter movieListAdapter;

    @Inject
    CategorySpinnerAdapter categorySpinnerAdapter;

    private ItemTouchHelper itemTouchHelper;

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_movie_list;
    }

    @Override
    protected ActivityComponent getInitializeCompoent() {
        return DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(new ActivityModule(this))
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
        movieListPresenter.setView(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(movieListAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(movieListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        categorySpinnerAdapter.addTotalData(getString(R.string.all));
        categorySpinner.setAdapter(categorySpinnerAdapter);
        RxAdapterView.selectionEvents(categorySpinner)
                .subscribe(event->movieListPresenter.loadMovieList());

        RxTextView.textChangeEvents(movieNameEditText)
                .subscribe(event->movieListPresenter.loadMovieList());

        String[] haeSeens = {getString(R.string.all), getString(R.string.not_have_seen), getString(R.string.have_seen)};
        haveSeenSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, haeSeens));
        RxAdapterView.selectionEvents(haveSeenSpinner)
                .subscribe(event->movieListPresenter.loadMovieList());

        RxView.clicks(addMovieBtn)
                .subscribe((v)->{
                    //동영상 등록 화면 이동
                    movieListPresenter.addMovie(movieNameEditText.getText().toString(), (Category) categorySpinner.getSelectedItem());
                    movieNameEditText.setText(null);
                });

        movieListPresenter.loadMovieList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        movieListAdapter.notifyDataSetChanged();
    }

    public static Intent getIntent(Context context){
        return new Intent(context, MovieListActivity.class);
    }

    @Override
    public String getName() {
        return movieNameEditText.getText().toString();
    }

    @Override
    public long getCategoryId() {
        return ((Category)categorySpinner.getSelectedItem()).getId();
    }

    @Override
    public Boolean getHaveSeen() {
        Boolean haveSeen = null;
        int position = haveSeenSpinner.getSelectedItemPosition();
        switch (position) {
            case 1:
                haveSeen = false;
                break;
            case 2:
                haveSeen = true;
                break;
        }
        return haveSeen;
    }

    @Override
    public void showList(RealmResults<Movie> movieRealmResults) {
        movieListAdapter.setMovieRealmResults(movieRealmResults);
        movieListAdapter.notifyDataSetChanged();
        searchCountTextView.setText(String.valueOf(movieRealmResults.size()));
    }
}
