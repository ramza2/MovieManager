package kr.co.ramza.moviemanager.presenter.impl;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.interactor.RepositoryInteractor;
import kr.co.ramza.moviemanager.presenter.CategoryListPresenter;
import kr.co.ramza.moviemanager.ui.view.CategoryListView;
import rx.Observable;

/**
 * Created by 전창현 on 2017-02-28.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class CategoryListPresenterImpl implements CategoryListPresenter {

    private final RepositoryInteractor repositoryInteractor;

    private CategoryListView categoryListView;

    @Inject
    public CategoryListPresenterImpl(RepositoryInteractor repositoryInteractor) {
        this.repositoryInteractor = repositoryInteractor;
    }

    @Override
    public void setView(CategoryListView categoryListView) {
        this.categoryListView = categoryListView;
    }

    @Override
    public void loadCategoryList() {
        categoryListView.showList(repositoryInteractor.getAllCategorys());
    }

    @Override
    public void addCategory(String categoryName) {
        Category category = new Category();
        category.setName(categoryName.trim());
        Observable<Category> categoryObservable = repositoryInteractor.addCategory(category);
        categoryObservable.subscribe(categoryResult ->loadCategoryList(), throwable -> this.categoryListView.showToast(R.string.failed_category_registration));
    }
}
