package kr.co.ramza.moviemanager.presenter.impl;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.presenter.LogPresenter;
import kr.co.ramza.moviemanager.ui.view.LogView;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class LogPresenterImpl implements LogPresenter {

    private RealmInteractor realmInteractor;
    private LogView logView;

    @Inject
    public LogPresenterImpl(RealmInteractor realmInteractor) {
        this.realmInteractor = realmInteractor;
    }

    @Override
    public void setView(LogView logView) {
        this.logView = logView;
    }

    @Override
    public void loadLogs() {
        logView.showLogs(realmInteractor.getAllLogs());
    }

    @Override
    public void clearLogs() {
        realmInteractor.clearLogs();
    }
}
