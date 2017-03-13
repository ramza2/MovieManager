package kr.co.ramza.moviemanager.presenter.impl;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.FirebaseInteractor;
import kr.co.ramza.moviemanager.model.interactor.RepositoryInteractor;
import kr.co.ramza.moviemanager.presenter.MainPresenter;
import kr.co.ramza.moviemanager.ui.view.MainView;
import kr.co.ramza.moviemanager.variable.Conts;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static rx.Observable.combineLatest;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MainPresenterImpl implements MainPresenter {

    private MainView mainView;

    private RepositoryInteractor repositoryInteractor;
    private FirebaseInteractor firebaseInteractor;

    @Inject
    public MainPresenterImpl(RepositoryInteractor repositoryInteractor, FirebaseInteractor firebaseInteractor) {
        this.repositoryInteractor = repositoryInteractor;
        this.firebaseInteractor = firebaseInteractor;
    }

    @Override
    public void setView(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void clearData() {
        repositoryInteractor.clearCategories();
        repositoryInteractor.clearMovies();
        repositoryInteractor.clearLogs();
    }

    @Override
    public void backup() {
        Observable<Boolean> fileObservable = Observable.defer(()->{
            boolean saveSuccess = false;
            try {
                File categoryFile = mainView.getFile(Conts.CATEGORY_FILE_NAME);
                repositoryInteractor.backup(repositoryInteractor.getAllCategorys(), categoryFile);
                File movieFile = mainView.getFile(Conts.MOVIE_FILE_NAME);
                repositoryInteractor.backup(repositoryInteractor.getAllMovies(), movieFile);
                File logFile = mainView.getFile(Conts.LOG_FILE_NAME);
                repositoryInteractor.backup(repositoryInteractor.getAllLogs(), logFile);
                saveSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Observable.just(saveSuccess);
        })
                .filter(success->success)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                    mainView.showStatus(R.string.complete_file_save);
                });

        File categoryFile = mainView.getFile(Conts.CATEGORY_FILE_NAME);
        Observable<UploadTask.TaskSnapshot> categoryObservable = firebaseInteractor.backup(Conts.CATEGORY_FILE_NAME, categoryFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    mainView.showStatus(R.string.category_saved_to_cloud);
                });
        File movieFile = mainView.getFile(Conts.MOVIE_FILE_NAME);
        Observable<UploadTask.TaskSnapshot> movieObservable = firebaseInteractor.backup(Conts.MOVIE_FILE_NAME, movieFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    mainView.showStatus(R.string.video_saved_to_cloud);
                });
        File logFile = mainView.getFile(Conts.LOG_FILE_NAME);
        Observable<UploadTask.TaskSnapshot> logObservable = firebaseInteractor.backup(Conts.LOG_FILE_NAME, logFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    mainView.showStatus(R.string.log_saved_to_cloud);
                });

        Observable cloudObservable = Observable.combineLatest(categoryObservable, movieObservable, logObservable, (taskSnapshot, taskSnapshot2, taskSnapshot3) -> null);

        fileObservable.flatMap(success -> cloudObservable)
                .doOnSubscribe(()->{
                    mainView.showStatus(R.string.start_backup);
                    mainView.showProgressDialog();
                })
                .doOnNext((o) -> {
                    mainView.showStatus(R.string.backup_complete);
                    mainView.dismissProgressDialog();
                })
                .doOnError(throwable -> {
                    mainView.showStatus(R.string.backup_failed);
                    mainView.dismissProgressDialog();
                })
                .subscribe();
    }

    @Override
    public void restore() {
        Observable<Boolean> fileObservable = Observable.defer(()->{
            boolean loadSuccess = false;
            try {
                File categoryFile = mainView.getFile(Conts.CATEGORY_FILE_NAME);
                if(categoryFile.exists()) repositoryInteractor.restore(categoryFile, Category.class);
                File movieFile = mainView.getFile(Conts.MOVIE_FILE_NAME);
                if(movieFile.exists()) repositoryInteractor.restore(movieFile, Movie.class);
                File logFile = mainView.getFile(Conts.LOG_FILE_NAME);
                if(logFile.exists()) repositoryInteractor.restore(logFile, Log.class);
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

        cloudObservable.flatMap(o -> fileObservable)
                .doOnSubscribe(()->{
                    mainView.showStatus(R.string.start_restore);
                    mainView.showProgressDialog();
                })
                .doOnNext((o) -> {
                    mainView.showStatus(R.string.restoration_complete);
                    mainView.dismissProgressDialog();
                })
                .subscribe();
    }
}
