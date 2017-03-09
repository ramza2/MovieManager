package kr.co.ramza.moviemanager.di.module;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kr.co.ramza.moviemanager.model.interactor.FirebaseInteractor;
import kr.co.ramza.moviemanager.model.interactor.RepositoryInteractor;

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
    public RepositoryInteractor provideRepositoryInteractor(){
        return new RepositoryInteractor(context);
    }

    @Singleton
    @Provides
    public FirebaseInteractor provideFirebaseInteractor(){
        return new FirebaseInteractor();
    }
}
