package kr.co.ramza.moviemanager.presenter;

import kr.co.ramza.moviemanager.ui.view.BatchRegistView;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface BatchRegistPresenter {
    void setView(BatchRegistView batchRegistView);
    void clearData();
    void backup();
    void restore();
}
