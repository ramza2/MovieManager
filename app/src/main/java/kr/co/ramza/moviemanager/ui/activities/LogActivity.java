package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import kr.co.ramza.moviemanager.MovieManagerApplication;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.presenter.impl.LogPresenterImpl;
import kr.co.ramza.moviemanager.ui.adapter.LogListAdapter;
import kr.co.ramza.moviemanager.ui.view.LogView;

public class LogActivity extends AppCompatActivity implements LogView{

    @Inject
    LogPresenterImpl logPresenter;

    LogListAdapter logListAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.initLogBtn)
    Button initLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);
        ((MovieManagerApplication)getApplicationContext()).getApplicationComponent().inject(this);

        logPresenter.setView(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        logListAdapter = new LogListAdapter();
        recyclerView.setAdapter(logListAdapter);
        logPresenter.loadLogs();

        RxView.clicks(initLogBtn)
                .subscribe(event->{
                    logPresenter.clearLogs();
                    logListAdapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        logListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLogs(RealmResults<Log> logRealmResults) {
        logListAdapter.setLogRealmResults(logRealmResults);
        logListAdapter.notifyDataSetChanged();
    }

    public static Intent getIntent(Context context){
        return new Intent(context, LogActivity.class);
    }

}