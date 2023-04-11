package kr.co.ramza.moviemanager.di.component;

import androidx.annotation.NonNull;

import dagger.Component;
import kr.co.ramza.moviemanager.di.annotation.PerActivity;
import kr.co.ramza.moviemanager.di.module.ViewModelModule;
import kr.co.ramza.moviemanager.ui.activities.MovieSearchActivity;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {
                ViewModelModule.class
        }
)
public interface ViewModelActivityComponent extends ActivityComponent {
    void inject(@NonNull MovieSearchActivity movieSearchActivity);
}
