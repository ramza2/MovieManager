package kr.co.ramza.moviemanager.ui.view;

import java.io.File;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MainView {
    File getFile(String fileName);
    void showStatus(int stingRes);
    void showProgressDialog();
    void dismissProgressDialog();
}
