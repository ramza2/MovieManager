package kr.co.ramza.moviemanager.di.module;

import android.content.Context;
import androidx.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kr.co.ramza.moviemanager.model.interactor.FirebaseInteractor;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;

/**
 * Created by 전창현 on 2017-02-27.
 * ACTIVE D&C
 * ramza@activednc.com
 */
@Module
public class RepositoryModule {
    private Context context;

    public RepositoryModule(@NonNull Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public RealmInteractor provideRealmInteractor(){
        return new RealmInteractor(context);
    }

    @Singleton
    @Provides
    public FirebaseInteractor provideFirebaseInteractor(){
        return new FirebaseInteractor();
    }
}
