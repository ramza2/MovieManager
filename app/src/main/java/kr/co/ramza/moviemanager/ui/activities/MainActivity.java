package kr.co.ramza.moviemanager.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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

import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.databinding.ActivityMainBinding;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerMainActivityComponent;
import kr.co.ramza.moviemanager.di.component.MainActivityComponent;
import kr.co.ramza.moviemanager.di.module.AuthModule;
import kr.co.ramza.moviemanager.presenter.MainPresenter;
import kr.co.ramza.moviemanager.ui.view.MainView;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity implements MainView {

    private ActivityMainBinding binding;

    private static final int RC_SIGN_IN = 9001;

    @Inject
    MainPresenter mainPresenter;

    ProgressDialog authDialog = null;
    ProgressDialog asyncDialog = null;

    private CompositeSubscription subscriptions = new CompositeSubscription();

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
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainPresenter.setView(this);

        authDialog = new ProgressDialog(this);
        authDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        authDialog.setMessage(getString(R.string.authorizing));
        authDialog.setCancelable(false);

        asyncDialog = new ProgressDialog(this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setCancelable(false);

        binding.goolgeSignInBtn.setSize(SignInButton.SIZE_STANDARD);

        // Set up click listeners
        binding.recommendBtn.setOnClickListener(v -> startActivity(MovieRecommendActivity.getIntent(this)));
        binding.categoryListBtn.setOnClickListener(v -> startActivity(CategoryListActivity.getIntent(this)));
        binding.movieListBtn.setOnClickListener(v -> startActivity(MovieListActivity.getIntent(this)));
        binding.logListBtn.setOnClickListener(v -> startActivity(LogActivity.getIntent(this)));
        binding.goolgeSignInBtn.setOnClickListener(v -> mainPresenter.signIn(RC_SIGN_IN));
        binding.signOutBtn.setOnClickListener(v -> mainPresenter.signOut());
        binding.disconnectBtn.setOnClickListener(v -> mainPresenter.revokeAccess());

        subscriptions.add(RxView.clicks(binding.clearDataBtn)
                .flatMap(x->dialog(this, R.string.init, R.string.question_init))
                .filter(x-> x == true)
                .subscribe(event->mainPresenter.clearData()));

        subscriptions.add(RxView.clicks(binding.backupBtn)
                .flatMap(x->dialog(this, R.string.backup, R.string.question_backup))
                .filter(x-> x == true)
                .subscribe(event -> {
                    mainPresenter.backup();
                }));

        subscriptions.add(RxView.clicks(binding.restoreBtn)
                .flatMap(x->dialog(this, R.string.restore, R.string.question_restore))
                .filter(x-> x == true)
                .subscribe(event-> mainPresenter.restore()));
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
    public void showAuthProgressDialog() {
        authDialog.show();
    }

    @Override
    public void dismissAuthProgressDialog() {
        if(authDialog != null && authDialog.isShowing()) authDialog.dismiss();
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

    public void showProgressDialog(@StringRes int stringRes) {
        asyncDialog.setMessage(getString(stringRes));
        asyncDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if(asyncDialog != null && asyncDialog.isShowing()) asyncDialog.dismiss();
    }

    @Override
    public void updateUI(FirebaseUser user) {
        if (user != null) {
            binding.statusTextView.setText(getString(R.string.signed_in_fmt, user.getDisplayName()));
            binding.signOutAndDisconnect.setVisibility(View.VISIBLE);
            binding.goolgeSignInBtn.setVisibility(View.GONE);
            binding.cloudLayout.setVisibility(View.VISIBLE);
        } else {
            binding.statusTextView.setText(R.string.signed_out);
            binding.signOutAndDisconnect.setVisibility(View.GONE);
            binding.goolgeSignInBtn.setVisibility(View.VISIBLE);
            binding.cloudLayout.setVisibility(View.GONE);
        }
    }

    public void showError(@StringRes int stringRes) {
        Toast.makeText(this, stringRes, Toast.LENGTH_LONG).show();
    }

    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriptions != null && !subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe();
        }
        binding = null;
    }
}
