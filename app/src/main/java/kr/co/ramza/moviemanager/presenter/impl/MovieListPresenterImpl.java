package kr.co.ramza.moviemanager.presenter.impl;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.presenter.MovieListPresenter;
import kr.co.ramza.moviemanager.ui.view.MovieListView;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MovieListPresenterImpl implements MovieListPresenter {

    private final RealmInteractor realmInteractor;

    private MovieListView movieListView;

    @Inject
    public MovieListPresenterImpl(RealmInteractor realmInteractor) {
        this.realmInteractor = realmInteractor;
    }

    @Override
    public void setView(MovieListView movieListView) {
        this.movieListView = movieListView;
    }

    @Override
    public void loadMovieList() {
        String name = movieListView.getName();
        String series = movieListView.getSeries();
        long categoryId = movieListView.getCategoryId();
        Boolean haveSeen = movieListView.getHaveSeen();
        movieListView.showList(realmInteractor.getMovies(name, series, categoryId, haveSeen));
    }

    @Override
    public void addMovie(Movie movie) {
        realmInteractor.addMovie(movie).subscribe();
    }
}
