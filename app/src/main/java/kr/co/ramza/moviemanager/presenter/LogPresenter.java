package kr.co.ramza.moviemanager.presenter;

import kr.co.ramza.moviemanager.ui.view.LogView;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface LogPresenter {
    void setView(LogView logView);
    void loadLogs();
    void clearLogs();
}
