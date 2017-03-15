package kr.co.ramza.moviemanager.presenter;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.ui.view.MovieRecommendView;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MovieRecommendPresenter {
    void setView(MovieRecommendView movieRecommendView);
    void startRecommend(Category category, boolean haveSeen, int searchType);
}
