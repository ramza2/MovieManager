package kr.co.ramza.moviemanager.ui.view;

import java.io.File;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface BatchRegistView {
    File getFile(String fileName);
    void showStatus(String status);
    void showProgressDialog();
    void dismissProgressDialog();
}
