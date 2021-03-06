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

public class Category extends RealmObject {
    @PrimaryKey
    private long id;
    @Required
    private String name;
    private Date registDt;
    private Date modifyDt;

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

    public Date getRegistDt() {
        return registDt;
    }

    public void setRegistDt(Date registDt) {
        this.registDt = registDt;
    }
}
