package kr.co.ramza.moviemanager.di.module;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

import dagger.Module;
import kr.co.ramza.moviemanager.ui.activities.MainActivity;

@Module
public class MainActivityModule {

    private WeakReference<MainActivity> activityWeakReference;

    public MainActivityModule(@NonNull MainActivity mainActivity) {
        this.activityWeakReference = new WeakReference<MainActivity>(mainActivity);
    }
}
