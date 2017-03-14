package kr.co.ramza.moviemanager.di.component;

import android.support.annotation.NonNull;

import dagger.Component;
import kr.co.ramza.moviemanager.di.annotation.PerActivity;
import kr.co.ramza.moviemanager.di.module.AuthModule;
import kr.co.ramza.moviemanager.di.module.PresenterModule;
import kr.co.ramza.moviemanager.ui.activities.MainActivity;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {
                AuthModule.class,
                PresenterModule.class
        }
)
public interface MainActivityComponent extends ActivityComponent {
    void inject(@NonNull MainActivity mainActivity);
}
