package kr.co.ramza.moviemanager.di.module;

import dagger.Binds;
import dagger.Module;
import kr.co.ramza.moviemanager.presenter.CategoryListPresenter;
import kr.co.ramza.moviemanager.presenter.LogPresenter;
import kr.co.ramza.moviemanager.presenter.MainPresenter;
import kr.co.ramza.moviemanager.presenter.MovieDetailPresenter;
import kr.co.ramza.moviemanager.presenter.MovieListPresenter;
import kr.co.ramza.moviemanager.presenter.MovieRecommendPresenter;
import kr.co.ramza.moviemanager.presenter.impl.CategoryListPresenterImpl;
import kr.co.ramza.moviemanager.presenter.impl.LogPresenterImpl;
import kr.co.ramza.moviemanager.presenter.impl.MainPresenterImpl;
import kr.co.ramza.moviemanager.presenter.impl.MovieDetailPresenterImpl;
import kr.co.ramza.moviemanager.presenter.impl.MovieListPresenterImpl;
import kr.co.ramza.moviemanager.presenter.impl.MovieRecommendPresenterImpl;

/**
 * Created by 전창현 on 2017-03-13.
 * ACTIVE D&C
 * ramza@activednc.com
 */
@Module
public abstract class PresenterModule {
    @Binds
    public abstract CategoryListPresenter bindCategoryListPresenter(CategoryListPresenterImpl
                                                            categoryListPresenter);

    @Binds
    public abstract LogPresenter bindLogPresenter(LogPresenterImpl logPresenter);

    @Binds
    public abstract MainPresenter bindMainPresenter(MainPresenterImpl mainPresenter);

    @Binds
    public abstract MovieDetailPresenter bindMovieDetailPresenter(MovieDetailPresenterImpl movieDetailPresenter);

    @Binds
    public abstract MovieListPresenter bindMovieListPresenter(MovieListPresenterImpl movieListPresenter);

    @Binds
    public abstract MovieRecommendPresenter bindMovieRecommendPresenter(MovieRecommendPresenterImpl movieRecommendPresenter);
}
