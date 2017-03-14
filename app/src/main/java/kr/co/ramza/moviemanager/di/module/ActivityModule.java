package kr.co.ramza.moviemanager.di.module;

import android.app.Activity;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import kr.co.ramza.moviemanager.di.annotation.PerActivity;

@Module
public class ActivityModule {

    private Activity activity;

    public ActivityModule(@NonNull Activity activity) {
        this.activity = activity;
    }

    @PerActivity
    @Provides
    Activity provideActivity() {
        return this.activity;
    }
}
