package kr.co.ramza.moviemanager.ui.view;

import androidx.annotation.StringRes;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MainView {
    File getFile(String fileName);
    void showAuthProgressDialog();
    void dismissAuthProgressDialog();
    void showStatus(@StringRes int stingRes);
    void showProgressDialog();
    void showToast(@StringRes int stringRes);
    void dismissProgressDialog();
    void updateUI(FirebaseUser user);
}