package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import kr.co.ramza.moviemanager.MovieManagerApplication;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.ApplicationComponent;
import kr.co.ramza.moviemanager.di.component.HasComponent;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public abstract class BaseActivity extends AppCompatActivity implements HasComponent<ActivityComponent> {

    protected abstract int getContentViewResource();

    protected abstract ActivityComponent getInitializeComponent();

    protected abstract void onInject(@Nullable ActivityComponent component);

    protected ActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResource());

        ButterKnife.bind(this);

        this.component = getInitializeComponent();
        if (this.component != null) {
            onInject(this.component);
        }
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((MovieManagerApplication) getApplication()).getApplicationComponent();
    }

    @Override
    public ActivityComponent getComponent() {
        return this.component;
    }

    Observable<Boolean> dialog(Context context, @StringRes int title, @StringRes int message) {
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

    Observable<Boolean> dialog(Context context, String title, String message, View view) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            final AlertDialog.Builder adb = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        subscriber.onNext(false);
                        subscriber.onCompleted();
                    });
            if(view != null){
                adb.setView(view);
            }
            AlertDialog ad = adb.create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }
}
