package kr.co.ramza.moviemanager.presenter.impl;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.presenter.MovieRecommendPresenter;
import kr.co.ramza.moviemanager.ui.view.MovieRecommendView;
import kr.co.ramza.moviemanager.variable.Conts;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MovieRecommendPresenterImpl implements MovieRecommendPresenter {
    private RealmInteractor realmInteractor;

    private MovieRecommendView movieRecommendView;

    @Inject
    public MovieRecommendPresenterImpl(RealmInteractor realmInteractor) {
        this.realmInteractor = realmInteractor;
    }

    @Override
    public void setView(MovieRecommendView movieRecommendView) {
        this.movieRecommendView = movieRecommendView;
    }

    @Override
    public void startRecommend(Category category, boolean haveSeen, int searchType) {
        Movie movie = null;
        int currentSearchType = Conts.SEARCH_TYPE_SEQUENCE;
        switch (searchType) {
            case Conts.SEARCH_TYPE_SEQUENCE:
                currentSearchType = Conts.SEARCH_TYPE_SEQUENCE;
                movie = realmInteractor.getFirstMovie(null, category.getId(), haveSeen);
                break;
            case Conts.SEARCH_TYPE_RANDOM:
                currentSearchType = Conts.SEARCH_TYPE_RANDOM;
                movie = realmInteractor.getRandomMovie(null, category.getId(), haveSeen);
                break;
            case Conts.SEARCH_TYPE_MIX:
                Log lastLog = realmInteractor.getLastLog(category.getId());
                if(lastLog != null){
                    if(lastLog.getSearchType() == Conts.SEARCH_TYPE_SEQUENCE){
                        currentSearchType = Conts.SEARCH_TYPE_RANDOM;
                        movie = realmInteractor.getRandomMovie(null, category.getId(), haveSeen);
                    }else{
                        currentSearchType = Conts.SEARCH_TYPE_SEQUENCE;
                        movie = realmInteractor.getFirstMovie(null, category.getId(), haveSeen);
                    }
                }else{
                    currentSearchType = Conts.SEARCH_TYPE_SEQUENCE;
                    movie = realmInteractor.getFirstMovie(null, category.getId(), haveSeen);
                }
                break;
        }

        if(movie != null){
            movieRecommendView.showRecommendMovie(movie);
            Log newLog = new Log();
            newLog.setMovie(movie);
            newLog.setSearchType(currentSearchType);
            realmInteractor.addLog(newLog).subscribe();
        }
    }
}
