package kr.co.ramza.moviemanager.presenter.impl;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.RepositoryInteractor;
import kr.co.ramza.moviemanager.presenter.MovieListPresenter;
import kr.co.ramza.moviemanager.ui.view.MovieListView;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MovieListPresenterImpl implements MovieListPresenter {

    private final RepositoryInteractor repositoryInteractor;

    private MovieListView movieListView;

    @Inject
    public MovieListPresenterImpl(RepositoryInteractor repositoryInteractor) {
        this.repositoryInteractor = repositoryInteractor;
    }

    @Override
    public void setView(MovieListView movieListView) {
        this.movieListView = movieListView;
    }

    @Override
    public void loadMovieList() {
        String name = movieListView.getName();
        long categoryId = movieListView.getCategoryId();
        Boolean haveSeen = movieListView.getHaveSeen();
        movieListView.showList(repositoryInteractor.getMovies(name, categoryId, haveSeen));
    }

    @Override
    public void addMovie(String name, Category category) {
        if(category.getId() > 0 && !name.trim().equals("")){
            Movie newMovie = new Movie();
            newMovie.setName(name.trim());
            newMovie.setCategory(category);
            repositoryInteractor.addMovie(newMovie).subscribe();
        }
    }
}
