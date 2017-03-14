package kr.co.ramza.moviemanager.model.interactor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import kr.co.ramza.moviemanager.R;

/**
 * Created by 전창현 on 2017-03-13.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class FirebaseAuthInteractor {
    private FragmentActivity fragmentActivity;

    private GoogleApiClient googleApiClient;

    private FirebaseAuth firebaseAuth;

    public FirebaseAuthInteractor(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public void init(GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragmentActivity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity , onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void addAuthStateListener(FirebaseAuth.AuthStateListener authStateListener){
        if(firebaseAuth != null) firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void removeAuthStateListener(FirebaseAuth.AuthStateListener authStateListener){
        if(firebaseAuth != null) firebaseAuth.removeAuthStateListener(authStateListener);
    }

    public void signIn(int requestCode) {
        if(googleApiClient != null){
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            fragmentActivity.startActivityForResult(signInIntent, requestCode);
        }
    }

    public void signOut(@NonNull ResultCallback resultCallback) {
        if(firebaseAuth != null) firebaseAuth.signOut();

        if(googleApiClient != null){
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(resultCallback);
        }
    }

    public void revokeAccess(@NonNull ResultCallback resultCallback) {
        if(firebaseAuth != null) firebaseAuth.signOut();

        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(resultCallback);
    }

    public void signInWithCredential(GoogleSignInAccount acct, OnCompleteListener<AuthResult> onCompleteListener){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(fragmentActivity, onCompleteListener);
    }
}
