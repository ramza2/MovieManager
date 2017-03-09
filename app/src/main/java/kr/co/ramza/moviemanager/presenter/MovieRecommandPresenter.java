package kr.co.ramza.moviemanager.presenter;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.ui.view.MovieRecommandView;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MovieRecommandPresenter {
    void setView(MovieRecommandView movieRecommandView);
    void startRecommand(Category category, boolean haveSeen, int searchType);
}
