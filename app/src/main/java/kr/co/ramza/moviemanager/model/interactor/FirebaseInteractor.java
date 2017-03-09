package kr.co.ramza.moviemanager.model.interactor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kelvinapps.rxfirebase.RxFirebaseStorage;

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
        Observable<UploadTask.TaskSnapshot> observable = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            StorageReference fileRef = storageRef.child(user.getUid()).child(fileName);
            try {
                InputStream inputStream = new FileInputStream(file);
                observable = RxFirebaseStorage.putStream(fileRef, inputStream);
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
            observable = RxFirebaseStorage.getFile(fileRef, file);
        }

        return observable;
    }
}
