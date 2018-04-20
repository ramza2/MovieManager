package kr.co.ramza.moviemanager.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by 전창현 on 2017-02-27.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class Movie extends RealmObject {
    @PrimaryKey
    private long id;
    private Category category;
    @Required
    private String name;
    private String series;
    private boolean haveSeen;
    private float starNum;
    private Date registDt;
    private Date modifyDt;

    public Movie() {
    }

    public Movie(Category category, String name, String series) {
        this.category = category;
        this.name = name;
        this.series = series;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isHaveSeen() {
        return haveSeen;
    }

    public void setHaveSeen(boolean haveSeen) {
        this.haveSeen = haveSeen;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getModifyDt() {
        return modifyDt;
    }

    public void setModifyDt(Date modifyDt) {
        this.modifyDt = modifyDt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Date getRegistDt() {
        return registDt;
    }

    public void setRegistDt(Date registDt) {
        this.registDt = registDt;
    }

    public float getStarNum() {
        return starNum;
    }

    public void setStarNum(float starNum) {
        this.starNum = starNum;
    }
}
