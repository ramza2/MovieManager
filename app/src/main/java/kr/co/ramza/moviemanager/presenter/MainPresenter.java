package kr.co.ramza.moviemanager.presenter;

import kr.co.ramza.moviemanager.ui.view.MainView;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MainPresenter {
    void setView(MainView mainView);
    void clearData();
    void backup();
    void restore();
}
