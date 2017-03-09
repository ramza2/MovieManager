package kr.co.ramza.moviemanager.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 전창현 on 2017-02-27.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class Log extends RealmObject {
    @PrimaryKey
    private long id;
    private Date logDt;
    private Movie movie;
    private int searchType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getLogDt() {
        return logDt;
    }

    public void setLogDt(Date logDt) {
        this.logDt = logDt;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }
}
