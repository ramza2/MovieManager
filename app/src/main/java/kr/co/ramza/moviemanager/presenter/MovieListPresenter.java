package kr.co.ramza.moviemanager.presenter;

import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.ui.view.MovieListView;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MovieListPresenter {
    void setView(MovieListView movieListView);
    void loadMovieList();
    void addMovie(Movie movie);
}
