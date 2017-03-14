package kr.co.ramza.moviemanager.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import kr.co.ramza.moviemanager.MovieManagerApplication;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.ApplicationComponent;
import kr.co.ramza.moviemanager.di.component.HasComponent;

public abstract class BaseActivity extends AppCompatActivity implements HasComponent<ActivityComponent> {

    protected abstract int getContentViewResource();

    protected abstract ActivityComponent getInitializeCompoent();

    protected abstract void onInject(@Nullable ActivityComponent component);

    protected ActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResource());

        ButterKnife.bind(this);

        this.component = getInitializeCompoent();
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
}
