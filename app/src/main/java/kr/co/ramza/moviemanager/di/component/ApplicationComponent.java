package kr.co.ramza.moviemanager.di.component;

import javax.inject.Singleton;

import dagger.Component;
import kr.co.ramza.moviemanager.di.module.RepositoryModule;
import kr.co.ramza.moviemanager.model.interactor.FirebaseInteractor;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;

/**
 * Created by 전창현 on 2017-02-27.
 * ACTIVE D&C
 * ramza@activednc.com
 */
@Singleton
@Component(
        modules = {RepositoryModule.class}
)
public interface ApplicationComponent {
    RealmInteractor getRealmInteractor();
    FirebaseInteractor getFirebaseInteractor();
}
