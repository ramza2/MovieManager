package kr.co.ramza.moviemanager.presenter;

import android.content.Intent;

import kr.co.ramza.moviemanager.ui.view.MainView;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MainPresenter {
    void setView(MainView mainView);
    void onStart();
    void onStop();
    void signIn(int requestCode);
    void signOut();
    void revokeAccess();
    void processSignInResult(Intent data);
    void clearData();
    void backup();
    void restore();
}
