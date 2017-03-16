package kr.co.ramza.moviemanager.ui.view;

import java.util.List;

import kr.co.ramza.moviemanager.model.Movie;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MovieRecommendView {
    void showRecommendMovie(int searchType, List<Movie> movieRealmResults);
}
