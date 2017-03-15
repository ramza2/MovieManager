package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.RealmResults;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerActivityComponent;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.presenter.LogPresenter;
import kr.co.ramza.moviemanager.ui.adapter.LogListAdapter;
import kr.co.ramza.moviemanager.ui.helper.SimpleItemTouchHelperCallback;
import kr.co.ramza.moviemanager.ui.view.LogView;

public class LogActivity extends BaseActivity implements LogView{

    @Inject
    LogPresenter logPresenter;

    @Inject
    LogListAdapter logListAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.initLogBtn)
    Button initLogBtn;

    private ItemTouchHelper itemTouchHelper;

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_log;
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

        logPresenter.setView(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(logListAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(logListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        RxView.clicks(initLogBtn)
                .subscribe(event->{
                    logPresenter.clearLogs();
                    logListAdapter.notifyDataSetChanged();
                });

        logPresenter.loadLogs();
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
