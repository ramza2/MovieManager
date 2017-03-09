package kr.co.ramza.moviemanager.ui.view;

import io.realm.RealmResults;
import kr.co.ramza.moviemanager.model.Log;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface LogView {
    void showLogs(RealmResults<Log> logRealmResults);
}
