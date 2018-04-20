package kr.co.ramza.moviemanager.presenter;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.ui.view.MovieDetailView;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MovieDetailPresenter {
    void setView(MovieDetailView movieDetailView);
    void loadMovie(long Id);
    void loadOrgMovie();
    void modifyMovie(String name, String series, Category category, boolean haveSeen, float starNum);
    void deleteMovie();
}
