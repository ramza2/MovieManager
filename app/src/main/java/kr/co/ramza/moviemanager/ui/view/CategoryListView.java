package kr.co.ramza.moviemanager.ui.view;

import android.support.annotation.StringRes;

import io.realm.RealmResults;
import kr.co.ramza.moviemanager.model.Category;

/**
 * Created by 전창현 on 2017-02-28.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface CategoryListView {
    void showList(RealmResults<Category> categoryRealmResults);
    void showToast(@StringRes int stringRes);
}
