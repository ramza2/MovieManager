package kr.co.ramza.moviemanager.model.interactor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import rx.Observable;

/**
 * Created by 전창현 on 2017-03-08.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class FirebaseInteractor {

    private StorageReference storageRef;

    public FirebaseInteractor() {
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public Observable<UploadTask.TaskSnapshot> backup(String fileName , File file){
        Observable<UploadTask.TaskSnapshot> observable = Observable.just(null);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            StorageReference fileRef = storageRef.child(user.getUid()).child(fileName);
            try {
                InputStream inputStream = new FileInputStream(file);
                observable = Observable.create(subscriber -> {
                    UploadTask task = fileRef.putStream(inputStream);
                    task.addOnSuccessListener(taskSnapshot -> {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(taskSnapshot);
                            subscriber.onCompleted();
                        }
                    }).addOnFailureListener(e -> {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(e);
                        }
                    });
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return observable;
    }

    public Observable<FileDownloadTask.TaskSnapshot> restore(String fileName , File file){
        Observable<FileDownloadTask.TaskSnapshot> observable = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            StorageReference fileRef = storageRef.child(user.getUid()).child(fileName);
            observable = Observable.create(subscriber -> {
                FileDownloadTask task = fileRef.getFile(file);
                task.addOnSuccessListener(taskSnapshot -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(taskSnapshot);
                        subscriber.onCompleted();
                    }
                }).addOnFailureListener(e -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                });
            });
        }

        return observable;
    }
}
