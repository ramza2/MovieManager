package kr.co.ramza.moviemanager.presenter.impl;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.presenter.CategoryListPresenter;
import kr.co.ramza.moviemanager.ui.view.CategoryListView;
import rx.Observable;

/**
 * Created by 전창현 on 2017-02-28.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class CategoryListPresenterImpl implements CategoryListPresenter {

    private final RealmInteractor realmInteractor;

    private CategoryListView categoryListView;

    @Inject
    public CategoryListPresenterImpl(RealmInteractor realmInteractor) {
        this.realmInteractor = realmInteractor;
    }

    @Override
    public void setView(CategoryListView categoryListView) {
        this.categoryListView = categoryListView;
    }

    @Override
    public void loadCategoryList() {
        categoryListView.showList(realmInteractor.getAllCategories());
    }

    @Override
    public void addCategory(String categoryName) {
        Category category = new Category();
        category.setName(categoryName.trim());
        Observable<Category> categoryObservable = realmInteractor.addCategory(category);
        categoryObservable.subscribe(categoryResult ->loadCategoryList(), throwable -> this.categoryListView.showToast(R.string.failed_category_registration));
    }
}
