package kr.co.ramza.moviemanager.ui.view;

import io.realm.RealmResults;
import kr.co.ramza.moviemanager.model.Movie;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public interface MovieListView {
    String getName();
    long getCategoryId();
    Boolean getHaveSeen();
    void showList(RealmResults<Movie> movieRealmResults);
}
