package kr.co.ramza.moviemanager.presenter.impl;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.FirebaseInteractor;
import kr.co.ramza.moviemanager.model.interactor.RepositoryInteractor;
import kr.co.ramza.moviemanager.presenter.BatchRegistPresenter;
import kr.co.ramza.moviemanager.ui.view.BatchRegistView;
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

public class BatchRegistPresenterImpl implements BatchRegistPresenter {

    private BatchRegistView batchRegistView;

    private RepositoryInteractor repositoryInteractor;
    private FirebaseInteractor firebaseInteractor;

    @Inject
    public BatchRegistPresenterImpl(RepositoryInteractor repositoryInteractor, FirebaseInteractor firebaseInteractor) {
        this.repositoryInteractor = repositoryInteractor;
        this.firebaseInteractor = firebaseInteractor;
    }

    @Override
    public void setView(BatchRegistView batchRegistView) {
        this.batchRegistView = batchRegistView;
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
                File categoryFile = batchRegistView.getFile(Conts.CATEGORY_FILE_NAME);
                repositoryInteractor.backup(repositoryInteractor.getAllCategorys(), categoryFile);
                File movieFile = batchRegistView.getFile(Conts.MOVIE_FILE_NAME);
                repositoryInteractor.backup(repositoryInteractor.getAllMovies(), movieFile);
                File logFile = batchRegistView.getFile(Conts.LOG_FILE_NAME);
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
                    batchRegistView.showStatus("파일 저장 완료");
                });

        File categoryFile = batchRegistView.getFile(Conts.CATEGORY_FILE_NAME);
        Observable<UploadTask.TaskSnapshot> categoryObservable = firebaseInteractor.backup(Conts.CATEGORY_FILE_NAME, categoryFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    batchRegistView.showStatus("Category 클라우드 저장 완료");
                });
        File movieFile = batchRegistView.getFile(Conts.MOVIE_FILE_NAME);
        Observable<UploadTask.TaskSnapshot> movieObservable = firebaseInteractor.backup(Conts.MOVIE_FILE_NAME, movieFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    batchRegistView.showStatus("Movie 클라우드 저장 완료");
                });
        File logFile = batchRegistView.getFile(Conts.LOG_FILE_NAME);
        Observable<UploadTask.TaskSnapshot> logObservable = firebaseInteractor.backup(Conts.LOG_FILE_NAME, logFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    batchRegistView.showStatus("Log 클라우드 저장 완료");
                });

        Observable cloudObservable = Observable.combineLatest(categoryObservable, movieObservable, logObservable, (taskSnapshot, taskSnapshot2, taskSnapshot3) -> null);

        fileObservable.flatMap(success -> cloudObservable)
                .doOnSubscribe(()->{
                    batchRegistView.showStatus("백업 시작");
                    batchRegistView.showProgressDialog();
                })
                .doOnNext((o) -> {
                    batchRegistView.showStatus("백업 완료");
                    batchRegistView.dismissProgressDialog();
                })
                .subscribe();
    }

    @Override
    public void restore() {
        Observable<Boolean> fileObservable = Observable.defer(()->{
            boolean loadSuccess = false;
            try {
                File categoryFile = batchRegistView.getFile(Conts.CATEGORY_FILE_NAME);
                if(categoryFile.exists()) repositoryInteractor.restore(categoryFile, Category.class);
                File movieFile = batchRegistView.getFile(Conts.MOVIE_FILE_NAME);
                if(movieFile.exists()) repositoryInteractor.restore(movieFile, Movie.class);
                File logFile = batchRegistView.getFile(Conts.LOG_FILE_NAME);
                if(logFile.exists()) repositoryInteractor.restore(logFile, Log.class);
                loadSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Observable.just(loadSuccess);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        File categoryFile = batchRegistView.getFile(Conts.CATEGORY_FILE_NAME);
        Observable<FileDownloadTask.TaskSnapshot> categoryObservable = firebaseInteractor.restore(Conts.CATEGORY_FILE_NAME, categoryFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    batchRegistView.showStatus("Category 클라우드 파일 복원 완료");
                });

        File movieFile = batchRegistView.getFile(Conts.MOVIE_FILE_NAME);
        Observable<FileDownloadTask.TaskSnapshot> movieObservable = firebaseInteractor.restore(Conts.MOVIE_FILE_NAME, movieFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    batchRegistView.showStatus("Movie 클라우드 파일 복원 완료");
                });
        File logFile = batchRegistView.getFile(Conts.LOG_FILE_NAME);
        Observable<FileDownloadTask.TaskSnapshot> logObservable = firebaseInteractor.restore(Conts.LOG_FILE_NAME, logFile)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(taskSnapshot -> {
                    batchRegistView.showStatus("Log 클라우드 파일 복원 완료");
                });

        Observable cloudObservable = combineLatest(categoryObservable, movieObservable, logObservable, (taskSnapshot, taskSnapshot2, taskSnapshot3) -> null);

        cloudObservable.flatMap(o -> fileObservable)
                .doOnSubscribe(()->{
                    batchRegistView.showStatus("복원 시작");
                    batchRegistView.showProgressDialog();
                })
                .doOnNext((o) -> {
                    batchRegistView.showStatus("복원 완료");
                    batchRegistView.dismissProgressDialog();
                })
                .subscribe();
    }
}
