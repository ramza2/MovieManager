package kr.co.ramza.moviemanager.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jakewharton.rxbinding.view.RxView;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ramza.moviemanager.MovieManagerApplication;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.presenter.MainPresenter;
import kr.co.ramza.moviemanager.ui.view.MainView;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, MainView {

    private GoogleApiClient googleApiClient;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @BindView(R.id.goolgeSignInBtn)
    SignInButton signInButton;
    @BindView(R.id.signOutBtn)
    Button signOutBtn;
    @BindView(R.id.disconnectBtn)
    Button disconnectBtn;
    @BindView(R.id.statusTextView)
    TextView statusTextView;
    @BindView(R.id.sign_out_and_disconnect)
    LinearLayout sign_out_and_disconnect;

    @BindView(R.id.cloudLayout)
    LinearLayout cloudLayout;
    @BindView(R.id.backupBtn)
    Button backupBtn;
    @BindView(R.id.restoreBtn)
    Button restoreBtn;
    @BindView(R.id.clearDataBtn)
    Button clearDataBtn;

    private static final int RC_SIGN_IN = 9001;

    @Inject
    MainPresenter mainPresenter;

    ProgressDialog asyncDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((MovieManagerApplication) getApplicationContext()).getApplicationComponent().inject(this);

        mainPresenter.setView(this);

        asyncDialog = new ProgressDialog(this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage(getString(R.string.authorizing));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };

        signInButton.setSize(SignInButton.SIZE_STANDARD);

        RxView.clicks(clearDataBtn)
                .flatMap(x->dialog(this, R.string.init, R.string.question_init))
                .filter(x-> x == true)
                .subscribe(event->mainPresenter.clearData());

        RxView.clicks(backupBtn)
                .flatMap(x->dialog(this, R.string.backup, R.string.question_backup))
                .filter(x-> x == true)
                .subscribe(event -> {
                    mainPresenter.backup();
                });

        RxView.clicks(restoreBtn)
                .flatMap(x->dialog(this, R.string.restore, R.string.question_restore))
                .filter(x-> x == true)
                .subscribe(event->{
                    mainPresenter.restore();
                });
    }

    Observable<Boolean> dialog(Context context, int title, int message) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        subscriber.onNext(false);
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(authListener != null){
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }

    @OnClick({R.id.recommandBtn, R.id.categoryListBtn, R.id.movieListBtn, R.id.logListBtn, R.id.goolgeSignInBtn, R.id.signOutBtn, R.id.disconnectBtn})
    public void onClick(View view){
        int id = view.getId();
        switch (id) {
            case R.id.recommandBtn:
                startActivity(MovieRecommandActivity.getIntent(this));
                break;
            case R.id.categoryListBtn:
                startActivity(CategoryListActivity.getIntent(this));
                break;
            case R.id.movieListBtn:
                startActivity(MovieListActivity.getIntent(this));
                break;
            case R.id.logListBtn:
                startActivity(LogActivity.getIntent(this));
                break;
            case R.id.goolgeSignInBtn:
                signIn();
                break;
            case R.id.signOutBtn:
                signOut();
                break;
            case R.id.disconnectBtn:
                revokeAccess();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // [START_EXCLUDE silent]
        showStatus(getString(R.string.authorizing));
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        dismissProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        firebaseAuth.signOut();

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void revokeAccess() {
        firebaseAuth.signOut();

        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public File getFile(String fileName) {
        return new File(getFilesDir(), fileName);
    }

    @Override
    public void showStatus(String status) {
        asyncDialog.setMessage(status);
    }

    @Override
    public void showProgressDialog() {
        asyncDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if(asyncDialog != null && asyncDialog.isShowing()) asyncDialog.dismiss();
    }

    private void updateUI(FirebaseUser user) {
        dismissProgressDialog();
        if (user != null) {
            statusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));

            cloudLayout.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            sign_out_and_disconnect.setVisibility(View.VISIBLE);
        } else {
            statusTextView.setText(R.string.signed_out);

            cloudLayout.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
            sign_out_and_disconnect.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
