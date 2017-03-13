package kr.co.ramza.moviemanager.di.component;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Component;
import kr.co.ramza.moviemanager.di.module.PresenterModule;
import kr.co.ramza.moviemanager.di.module.RepositoryModule;
import kr.co.ramza.moviemanager.ui.activities.CategoryListActivity;
import kr.co.ramza.moviemanager.ui.activities.LogActivity;
import kr.co.ramza.moviemanager.ui.activities.MainActivity;
import kr.co.ramza.moviemanager.ui.activities.MovieDetailActivity;
import kr.co.ramza.moviemanager.ui.activities.MovieListActivity;
import kr.co.ramza.moviemanager.ui.activities.MovieRecommandActivity;

/**
 * Created by 전창현 on 2017-02-27.
 * ACTIVE D&C
 * ramza@activednc.com
 */
@Singleton
@Component(
        modules = {RepositoryModule.class, PresenterModule.class}
)
public interface ApplicationComponent {
    void inject(@NonNull MainActivity mainActivity);
    void inject(@NonNull CategoryListActivity categoryListActivity);
    void inject(@NonNull MovieListActivity movieListActivity);
    void inject(@NonNull MovieDetailActivity movieDetailActivity);
    void inject(@NonNull MovieRecommandActivity movieRecommandActivity);
    void inject(@NonNull LogActivity logActivity);
}
