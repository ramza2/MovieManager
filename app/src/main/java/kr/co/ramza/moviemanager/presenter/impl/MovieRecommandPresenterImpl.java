package kr.co.ramza.moviemanager.presenter.impl;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.model.Movie;
import kr.co.ramza.moviemanager.model.interactor.RepositoryInteractor;
import kr.co.ramza.moviemanager.presenter.MovieRecommandPresenter;
import kr.co.ramza.moviemanager.ui.view.MovieRecommandView;
import kr.co.ramza.moviemanager.variable.Conts;

/**
 * Created by 전창현 on 2017-03-03.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class MovieRecommandPresenterImpl implements MovieRecommandPresenter {
    private RepositoryInteractor repositoryInteractor;

    private MovieRecommandView movieRecommandView;

    @Inject
    public MovieRecommandPresenterImpl(RepositoryInteractor repositoryInteractor) {
        this.repositoryInteractor = repositoryInteractor;
    }

    @Override
    public void setView(MovieRecommandView movieRecommandView) {
        this.movieRecommandView = movieRecommandView;
    }

    @Override
    public void startRecommand(Category category, boolean haveSeen, int searchType) {
        Movie movie = null;
        int currentSearchType = Conts.SEARCH_TYPE_SEQUENCE;
        switch (searchType) {
            case Conts.SEARCH_TYPE_SEQUENCE:
                currentSearchType = Conts.SEARCH_TYPE_SEQUENCE;
                movie = repositoryInteractor.getFirstMovie(null, category.getId(), haveSeen);
                break;
            case Conts.SEARCH_TYPE_RANDOM:
                currentSearchType = Conts.SEARCH_TYPE_RANDOM;
                movie = repositoryInteractor.getRandomMovie(null, category.getId(), haveSeen);
                break;
            case Conts.SEARCH_TYPE_MIX:
                Log lastLog = repositoryInteractor.getLastLog(category.getId());
                if(lastLog != null){
                    if(lastLog.getSearchType() == Conts.SEARCH_TYPE_SEQUENCE){
                        currentSearchType = Conts.SEARCH_TYPE_RANDOM;
                        movie = repositoryInteractor.getRandomMovie(null, category.getId(), haveSeen);
                    }else{
                        currentSearchType = Conts.SEARCH_TYPE_SEQUENCE;
                        movie = repositoryInteractor.getFirstMovie(null, category.getId(), haveSeen);
                    }
                }else{
                    currentSearchType = Conts.SEARCH_TYPE_SEQUENCE;
                    movie = repositoryInteractor.getFirstMovie(null, category.getId(), haveSeen);
                }
                break;
        }

        if(movie != null){
            movieRecommandView.showRecommandMovie(movie);
            Log newLog = new Log();
            newLog.setMovie(movie);
            newLog.setSearchType(currentSearchType);
            repositoryInteractor.addLog(newLog).subscribe();
        }
    }
}
