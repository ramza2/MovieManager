package kr.co.ramza.moviemanager.presenter.impl;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.FirebaseAuthInteractor;
import kr.co.ramza.moviemanager.model.interactor.FirebaseInteractor;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.presenter.MainPresenter;
import kr.co.ramza.moviemanager.ui.view.MainView;
import kr.co.ramza.moviemanager.variable.Conts;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static rx.Observable.combineLatest;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MainPresenterImpl implements MainPresenter, GoogleApiClient.OnConnectionFailedListener {

    private MainView mainView;

    private RealmInteractor realmInteractor;
    private FirebaseInteractor firebaseInteractor;
    private FirebaseAuthInteractor firebaseAuthInteractor;

    private FirebaseAuth.AuthStateListener authListener;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public MainPresenterImpl(RealmInteractor realmInteractor, FirebaseInteractor firebaseInteractor, FirebaseAuthInteractor firebaseAuthInteractor) {
        this.realmInteractor = realmInteractor;
        this.firebaseInteractor = firebaseInteractor;
        this.firebaseAuthInteractor = firebaseAuthInteractor;

        this.firebaseAuthInteractor.init(this);

        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            // [START_EXCLUDE]
            mainView.updateUI(user);
            // [END_EXCLUDE]
        };
    }

    @Override
    public void setView(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void onStart() {
        firebaseAuthInteractor.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        firebaseAuthInteractor.removeAuthStateListener(authListener);
    }

    @Override
    public void signIn(int requestCode) {
        firebaseAuthInteractor.signIn(requestCode);
    }

    @Override
    public void signOut() {
        firebaseAuthInteractor.signOut(result -> mainView.updateUI(null));
    }

    @Override
    public void revokeAccess() {
        firebaseAuthInteractor.revokeAccess(result -> mainView.updateUI(null));
    }

    @Override
    public void processSignInResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            // Google Sign In failed, update UI appropriately
            // [START_EXCLUDE]
            mainView.updateUI(null);
            // [END_EXCLUDE]
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // [START_EXCLUDE silent]
        mainView.showStatus(R.string.authorizing);
        mainView.showAuthProgressDialog();
        // [END_EXCLUDE]

        firebaseAuthInteractor.signInWithCredential(acct, task -> {

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
                mainView.showToast(R.string.authentication_failed);
            }
            // [START_EXCLUDE]
            mainView.dismissAuthProgressDialog();
            // [END_EXCLUDE]
        });
    }

    @Override
    public void clearData() {
        realmInteractor.clearCategories();
        realmInteractor.clearMovies();
        realmInteractor.clearLogs();
    }

    @Override
    public void backup() {
        Observable<Boolean> fileObservable = Observable.defer(()->{
            boolean saveSuccess = false;

            if(realmInteractor.getAllCategories().size() > 0 && realmInteractor.getAllMovies().size() > 0){
                try {
                    File categoryFile = mainView.getFile(Conts.CATEGORY_FILE_NAME);
                    realmInteractor.backup(realmInteractor.getAllCategories(), categoryFile);
                    File movieFile = mainView.getFile(Conts.MOVIE_FILE_NAME);
                    realmInteractor.backup(realmInteractor.getAllMovies(), movieFile);
                    File logFile = mainView.getFile(Conts.LOG_FILE_NAME);
                    realmInteractor.backup(realmInteractor.getAllLogs(), logFile);
                    saveSuccess = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Observable.just(saveSuccess);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        File categoryFile = mainView.getFile(Conts.CATEGORY_FILE_NAME);
        Observable<UploadTask.TaskSnapshot> categoryObservable = firebaseInteractor.backup(Conts.CATEGORY_FILE_NAME, categoryFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> mainView.showStatus(R.string.category_saved_to_cloud));
        File movieFile = mainView.getFile(Conts.MOVIE_FILE_NAME);
        Observable<UploadTask.TaskSnapshot> movieObservable = firebaseInteractor.backup(Conts.MOVIE_FILE_NAME, movieFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> mainView.showStatus(R.string.video_saved_to_cloud));
        File logFile = mainView.getFile(Conts.LOG_FILE_NAME);
        Observable<UploadTask.TaskSnapshot> logObservable = firebaseInteractor.backup(Conts.LOG_FILE_NAME, logFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> mainView.showStatus(R.string.log_saved_to_cloud));

        Observable cloudObservable = Observable.combineLatest(categoryObservable, movieObservable, logObservable, (taskSnapshot, taskSnapshot2, taskSnapshot3) -> null)
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(o -> {
                    mainView.showToast(R.string.backup_complete);
                    mainView.dismissProgressDialog();
                   return null;
                });

        subscriptions.add(fileObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(success -> {
                    if(!success){
                        mainView.showToast(R.string.backup_failed);
                        mainView.dismissProgressDialog();
                    }else{
                        mainView.showStatus(R.string.complete_file_save);
                    }
                   return success;
                })
                .subscribeOn(Schedulers.io())
                .filter(success -> success)
                .flatMap(success -> cloudObservable)
                .doOnSubscribe(()->{
                    mainView.showStatus(R.string.start_backup);
                    mainView.showProgressDialog();
                })
                .doOnError(throwable -> {
                    mainView.showToast(R.string.backup_failed);
                    mainView.dismissProgressDialog();
                })
                .subscribe());
    }

    @Override
    public void restore() {
        Observable<Boolean> fileObservable = Observable.defer(()->{
            boolean loadSuccess = false;
            try {
                File categoryFile = mainView.getFile(Conts.CATEGORY_FILE_NAME);
                if(categoryFile.exists()) realmInteractor.restore(categoryFile, Category.class);
                File movieFile = mainView.getFile(Conts.MOVIE_FILE_NAME);
                if(movieFile.exists()) realmInteractor.restore(movieFile, Movie.class);
                File logFile = mainView.getFile(Conts.LOG_FILE_NAME);
                if(logFile.exists()) realmInteractor.restore(logFile, Log.class);
                loadSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Observable.just(loadSuccess);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        File categoryFile = mainView.getFile(Conts.CATEGORY_FILE_NAME);
        Observable<FileDownloadTask.TaskSnapshot> categoryObservable = firebaseInteractor.restore(Conts.CATEGORY_FILE_NAME, categoryFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    mainView.showStatus(R.string.completed_category_cloud_file_restoration);
                });

        File movieFile = mainView.getFile(Conts.MOVIE_FILE_NAME);
        Observable<FileDownloadTask.TaskSnapshot> movieObservable = firebaseInteractor.restore(Conts.MOVIE_FILE_NAME, movieFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    mainView.showStatus(R.string.completed_video_cloud_file_restoration);
                });
        File logFile = mainView.getFile(Conts.LOG_FILE_NAME);
        Observable<FileDownloadTask.TaskSnapshot> logObservable = firebaseInteractor.restore(Conts.LOG_FILE_NAME, logFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    mainView.showStatus(R.string.completed_log_cloud_file_restoration);
                });

        Observable cloudObservable = combineLatest(categoryObservable, movieObservable, logObservable, (taskSnapshot, taskSnapshot2, taskSnapshot3) -> null);

        subscriptions.add(cloudObservable.flatMap(o -> fileObservable)
                .doOnSubscribe(()->{
                    mainView.showStatus(R.string.start_restore);
                    mainView.showProgressDialog();
                })
                .doOnNext((o) -> {
                    mainView.showStatus(R.string.restoration_complete);
                    mainView.dismissProgressDialog();
                })
                .subscribe());
    }

    @Override
    public void release() {
        subscriptions.unsubscribe();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mainView.showToast(R.string.google_play_services_error);
    }
}
