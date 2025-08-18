package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import io.realm.RealmResults;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.databinding.ActivityMovieListBinding;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerPresenterActivityComponent;
import kr.co.ramza.moviemanager.di.component.PresenterActivityComponent;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.presenter.MovieListPresenter;
import kr.co.ramza.moviemanager.ui.adapter.CategorySpinnerAdapter;
import kr.co.ramza.moviemanager.ui.adapter.MovieListAdapter;
import kr.co.ramza.moviemanager.ui.helper.SimpleItemTouchHelperCallback;
import kr.co.ramza.moviemanager.ui.view.ClearEditText;
import kr.co.ramza.moviemanager.ui.view.MovieListView;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class MovieListActivity extends BaseActivity implements MovieListView{

    private ActivityMovieListBinding binding;

    @Inject
    MovieListPresenter movieListPresenter;

    @Inject
    MovieListAdapter movieListAdapter;

    @Inject
    CategorySpinnerAdapter categorySpinnerAdapter;

    private ItemTouchHelper itemTouchHelper;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_movie_list;
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
        
        binding = ActivityMovieListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        movieListPresenter.setView(this);

        binding.recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(),
                layoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(dividerItemDecoration);

        binding.recyclerView.setAdapter(movieListAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(movieListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);

        categorySpinnerAdapter.addTotalData(getString(R.string.all));
        binding.categorySpinner.setAdapter(categorySpinnerAdapter);
        subscriptions.add(RxAdapterView.selectionEvents(binding.categorySpinner)
                .subscribe(event->movieListPresenter.loadMovieList()));

        subscriptions.add(RxTextView.textChangeEvents(binding.movieNameEditText)
                .subscribe(event->movieListPresenter.loadMovieList()));

        subscriptions.add(RxTextView.textChangeEvents(binding.seriesEditText)
                .subscribe(event->movieListPresenter.loadMovieList()));

        String[] hasSeen = {getString(R.string.all), getString(R.string.not_have_seen), getString(R.string.have_seen)};
        binding.haveSeenSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hasSeen));
        subscriptions.add(RxAdapterView.selectionEvents(binding.haveSeenSpinner)
                .subscribe(event->movieListPresenter.loadMovieList()));

        Observable<Category> categoryObservable =
                Observable.create((Observable.OnSubscribe<Category>) subscriber -> subscriber.onNext((Category) binding.categorySpinner.getSelectedItem()))
                        .filter(category -> {if(category == null || category.getId() <= 0){
                            Toast.makeText(MovieListActivity.this, R.string.please_select_category, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                            return true;
                        });

        Observable<String> movieNameObservable =
                Observable.create((Observable.OnSubscribe<String>) subscriber -> subscriber.onNext(binding.movieNameEditText.getText().toString().trim()))
                        .filter(movieName->{
                            if(movieName.equals("")){
                                Toast.makeText(MovieListActivity.this, R.string.please_input_video_name, Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            return true;
                        });

        Observable<String> seriesObservable =
                Observable.create(subscriber -> subscriber.onNext(binding.seriesEditText.getText().toString().trim()));

        subscriptions.add(RxView.clicks(binding.addMovieBtn)
                .flatMap(event -> Observable.zip(categoryObservable, movieNameObservable, seriesObservable, (category, name, series) -> new Movie(category, name, series)))
                .subscribe(movie -> {
                    movieListPresenter.addMovie(movie);
                    movieListAdapter.notifyDataSetChanged();
                    Toast.makeText(MovieListActivity.this, R.string.video_added, Toast.LENGTH_SHORT).show();
                }));

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
        return binding.movieNameEditText.getText().toString().trim();
    }

    @Override
    public String getSeries() {
        return binding.seriesEditText.getText().toString().trim();
    }

    @Override
    public long getCategoryId() {
        return ((Category)binding.categorySpinner.getSelectedItem()).getId();
    }

    @Override
    public Boolean getHaveSeen() {
        Boolean haveSeen = null;
        int position = binding.haveSeenSpinner.getSelectedItemPosition();
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
        binding.searchCountTextView.setText(String.valueOf(movieRealmResults.size()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscriptions != null && !subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe();
        }
        binding = null;
    }
}
