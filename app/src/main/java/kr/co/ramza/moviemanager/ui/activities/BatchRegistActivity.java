package kr.co.ramza.moviemanager.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ramza.moviemanager.MovieManagerApplication;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.presenter.impl.BatchRegistPresenterImpl;
import kr.co.ramza.moviemanager.ui.view.BatchRegistView;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public class BatchRegistActivity extends AppCompatActivity implements BatchRegistView{

    @BindView(R.id.clearDataBtn)
    Button clearDataBtn;

    @BindView(R.id.parseStatus)
    TextView parseStatus;

    @BindView(R.id.backupFileBtn)
    Button backupFileBtn;
    @BindView(R.id.restoreFileBtn)
    Button restoreFileBtn;

    @Inject
    BatchRegistPresenterImpl batchRegistPresenter;

    ProgressDialog asyncDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_regist);
        ButterKnife.bind(this);
        ((MovieManagerApplication) getApplicationContext()).getApplicationComponent().inject(this);
        batchRegistPresenter.setView(this);

        asyncDialog = new ProgressDialog(this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("작업중");

        RxView.clicks(clearDataBtn)
                .flatMap(x->dialog(this, R.string.init, R.string.question_init))
                .filter(x-> x == true)
                .subscribe(event->batchRegistPresenter.clearData());

        RxView.clicks(backupFileBtn)
                .flatMap(x->dialog(this, R.string.backup, R.string.question_backup))
                .filter(x-> x == true)
                .subscribe(event -> {
                    batchRegistPresenter.backup();
                });

        RxView.clicks(restoreFileBtn)
                .flatMap(x->dialog(this, R.string.restore, R.string.question_restore))
                .filter(x-> x == true)
                .subscribe(event->{
                    batchRegistPresenter.restore();
                });
    }

    Observable<Boolean> dialog(Context context, int title, int message) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        subscriber.onNext(false);
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }

    public static Intent getIntent(Context context){
        return new Intent(context, BatchRegistActivity.class);
    }

    @Override
    public File getFile(String fileName) {
        return new File(getFilesDir(), fileName);
    }

    @Override
    public void showStatus(String status) {
        parseStatus.setText(status);
    }

    @Override
    public void showProgressDialog() {
        asyncDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        asyncDialog.dismiss();
    }
}
