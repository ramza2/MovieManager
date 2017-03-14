package kr.co.ramza.moviemanager.di.component;

import android.support.annotation.NonNull;

import dagger.Component;
import kr.co.ramza.moviemanager.di.annotation.PerActivity;
import kr.co.ramza.moviemanager.di.module.PresenterModule;
import kr.co.ramza.moviemanager.ui.activities.CategoryListActivity;
import kr.co.ramza.moviemanager.ui.activities.LogActivity;
import kr.co.ramza.moviemanager.ui.activities.MovieDetailActivity;
import kr.co.ramza.moviemanager.ui.activities.MovieListActivity;
import kr.co.ramza.moviemanager.ui.activities.MovieRecommandActivity;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {
                PresenterModule.class
        }
)
public interface ActivityComponent {
    void inject(@NonNull CategoryListActivity categoryListActivity);
    void inject(@NonNull MovieListActivity movieListActivity);
    void inject(@NonNull MovieDetailActivity movieDetailActivity);
    void inject(@NonNull MovieRecommandActivity movieRecommandActivity);
    void inject(@NonNull LogActivity logActivity);
}
