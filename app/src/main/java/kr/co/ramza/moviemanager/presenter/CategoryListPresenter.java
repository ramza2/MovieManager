package kr.co.ramza.moviemanager.presenter;

import kr.co.ramza.moviemanager.ui.view.CategoryListView;

/**
 * Created by 전창현 on 2017-02-28.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface CategoryListPresenter {
    void setView(CategoryListView categoryListView);
    void loadCategoryList();
    void addCategory(String categoryName);
    void release();
}
