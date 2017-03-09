package kr.co.ramza.moviemanager.presenter.impl;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.RepositoryInteractor;
import kr.co.ramza.moviemanager.presenter.MovieDetailPresenter;
import kr.co.ramza.moviemanager.ui.view.MovieDetailView;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MovieDetailPresenterImpl implements MovieDetailPresenter {
    private RepositoryInteractor repositoryInteractor;

    private MovieDetailView movieDetailView;

    private Movie movie;

    @Inject
    public MovieDetailPresenterImpl(RepositoryInteractor repositoryInteractor) {
        this.repositoryInteractor = repositoryInteractor;
    }

    @Override
    public void setView(MovieDetailView movieDetailView) {
        this.movieDetailView = movieDetailView;
    }

    @Override
    public void loadMovie(long id) {
        this.movie = repositoryInteractor.getMovie(id);
        movieDetailView.showMovieInfo(movie);
    }

    @Override
    public void modifyMovie(String name, Category category, boolean haveSeen, float starNum) {
        repositoryInteractor.modifyMovieInfo(this.movie, name, category, haveSeen, starNum);
    }

    @Override
    public void deleteMovie() {
        repositoryInteractor.deleteMovie(this.movie);
    }
}
