package kr.co.ramza.moviemanager.presenter.impl;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.presenter.MovieDetailPresenter;
import kr.co.ramza.moviemanager.ui.view.MovieDetailView;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MovieDetailPresenterImpl implements MovieDetailPresenter {
    private RealmInteractor realmInteractor;

    private MovieDetailView movieDetailView;

    private Movie movie;

    private Movie origin;

    @Inject
    public MovieDetailPresenterImpl(RealmInteractor realmInteractor) {
        this.realmInteractor = realmInteractor;
    }

    @Override
    public void setView(MovieDetailView movieDetailView) {
        this.movieDetailView = movieDetailView;
    }

    @Override
    public void loadMovie(long id) {
        this.movie = realmInteractor.getMovie(id);
        this.origin = realmInteractor.getCopyiedMovie(movie);
        movieDetailView.showMovieInfo(movie);
    }

    @Override
    public void loadOrgMovie() {
        if(origin != null){
            movieDetailView.showMovieInfo(origin);
        }
    }

    @Override
    public void modifyMovie(String name, String series, Category category, boolean haveSeen, float starNum) {
        realmInteractor.modifyMovieInfo(this.movie, name, series, category, haveSeen, starNum);
    }

    @Override
    public void deleteMovie() {
        realmInteractor.deleteMovie(this.movie);
    }
}
