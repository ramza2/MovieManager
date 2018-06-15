package kr.co.ramza.moviemanager.di.component;

import dagger.Component;
import kr.co.ramza.moviemanager.di.annotation.PerActivity;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class
)
public interface ActivityComponent {
}
