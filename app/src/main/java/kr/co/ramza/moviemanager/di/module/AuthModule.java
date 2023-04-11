package kr.co.ramza.moviemanager.di.module;

import androidx.fragment.app.FragmentActivity;

import dagger.Module;
import dagger.Provides;
import kr.co.ramza.moviemanager.di.annotation.PerActivity;
import kr.co.ramza.moviemanager.model.interactor.FirebaseAuthInteractor;

/**
 * Created by 전창현 on 2017-03-14.
 * ACTIVE D&C
 * ramza@activednc.com
 */

@Module
public class AuthModule {
    private FragmentActivity fragmentActivity;

    public AuthModule(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    @PerActivity
    @Provides
    public FirebaseAuthInteractor provideFirebaseAuthInteractor(){
        return new FirebaseAuthInteractor(this.fragmentActivity);
    }
}
