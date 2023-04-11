package kr.co.ramza.moviemanager.di.component;

import androidx.annotation.NonNull;

import dagger.Component;
import kr.co.ramza.moviemanager.di.annotation.PerActivity;
import kr.co.ramza.moviemanager.ui.activities.MovieSearchDetailActivity;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class
)
public interface ActivityComponent {
    void inject(@NonNull MovieSearchDetailActivity movieSearchDetailActivity);
}
