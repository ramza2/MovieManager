package kr.co.ramza.moviemanager.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding.view.RxView;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerMainActivityComponent;
import kr.co.ramza.moviemanager.di.component.MainActivityComponent;
import kr.co.ramza.moviemanager.di.module.AuthModule;
import kr.co.ramza.moviemanager.presenter.MainPresenter;
import kr.co.ramza.moviemanager.ui.view.MainView;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public class MainActivity extends BaseActivity implements MainView {

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
    protected int getContentViewResource() {
        return R.layout.activity_main;
    }

    @Override
    protected ActivityComponent getInitializeComponent() {
        return DaggerMainActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .authModule(new AuthModule(this))
                .build();
    }

    @Override
    protected void onInject(@Nullable ActivityComponent component) {
        if (component != null) {
            ((MainActivityComponent)component).inject(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainPresenter.setView(this);

        asyncDialog = new ProgressDialog(this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage(getString(R.string.authorizing));

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
                .subscribe(event-> mainPresenter.restore());
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

         mainPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mainPresenter.onStop();
    }

    @OnClick({R.id.recommendBtn, R.id.categoryListBtn, R.id.movieListBtn, R.id.logListBtn, R.id.goolgeSignInBtn, R.id.signOutBtn, R.id.disconnectBtn})
    public void onClick(View view){
        int id = view.getId();
        switch (id) {
            case R.id.recommendBtn:
                startActivity(MovieRecommendActivity.getIntent(this));
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
                mainPresenter.signIn(RC_SIGN_IN);
                break;
            case R.id.signOutBtn:
                mainPresenter.signOut();
                break;
            case R.id.disconnectBtn:
                mainPresenter.revokeAccess();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            mainPresenter.processSignInResult(data);
        }
    }

    @Override
    public File getFile(String fileName) {
        return new File(getFilesDir(), fileName);
    }

    @Override
    public void showStatus(@StringRes int stingRes) {
        asyncDialog.setMessage(getString(stingRes));
    }

    @Override
    public void showProgressDialog() {
        asyncDialog.show();
    }

    @Override
    public void showToast(@StringRes int stringRes) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismissProgressDialog() {
        if(asyncDialog != null && asyncDialog.isShowing()) asyncDialog.dismiss();
    }

    @Override
    public void updateUI(FirebaseUser user) {
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
}
