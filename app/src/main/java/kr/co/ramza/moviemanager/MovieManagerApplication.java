package kr.co.ramza.moviemanager;

import android.app.Application;

import kr.co.ramza.moviemanager.di.component.ApplicationComponent;
import kr.co.ramza.moviemanager.di.component.DaggerApplicationComponent;
import kr.co.ramza.moviemanager.di.module.RepositoryModule;

/**
 * Created by 전창현 on 2017-02-27.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MovieManagerApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.applicationComponent = DaggerApplicationComponent.builder().repositoryModule(new RepositoryModule(this)).build();
    }

    public ApplicationComponent getApplicationComponent(){return this.applicationComponent;}
}
