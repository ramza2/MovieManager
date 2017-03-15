package kr.co.ramza.moviemanager.model.interactor;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Random;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.Log;
import kr.co.ramza.moviemanager.model.Movie;
import rx.Observable;

/**
 * Created by 전창현 on 2017-02-27.
 * ACTIVE D&C
 * ramza@activednc.com
 */
public class RealmInteractor {

    public RealmInteractor(Context context) {
        Realm.init(context);
    }

    public Observable<Category> addCategory(Category newCategory){
        Realm realm = Realm.getDefaultInstance();
        Observable<Category> categoryAddObserverble = Observable.just(newCategory)
                .map(category->{
                    category.setId(getNextKey(Category.class));
                    category.setRegistDt(new Date());
                    return category;
                })
                .doOnNext(category->{
                    realm.beginTransaction();
                    realm.copyToRealm(category);
                    realm.commitTransaction();
                });
        return categoryAddObserverble;
    }

    public void addCategoryFromJsonArray(JSONArray jsonArray){
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.createOrUpdateAllFromJson(Category.class, jsonArray);
            realm.commitTransaction();
        }catch (Exception e){
            realm.cancelTransaction();
            throw e;
        }
    }

    public RealmResults<Category> getAllCategories(){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Category.class).findAll();
    }

    public void modifyCategoryName(Category category, String categoryName){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        category.setModifyDt(new Date());
        category.setName(categoryName);
        realm.commitTransaction();
    }

    public void deleteCategory(Category category){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        category.deleteFromRealm();
        realm.commitTransaction();
    }

    public void clearCategories(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Category.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public Observable<Movie> addMovie(Movie newMovie){
        Realm realm = Realm.getDefaultInstance();
        Observable<Movie> movieAddObservable = Observable.just(newMovie)
                .map(movie->{
                    movie.setId(getNextKey(Movie.class));
                    movie.setRegistDt(new Date());
                    return movie;
                })
                .doOnNext(movie -> {
                    realm.beginTransaction();
                    realm.copyToRealm(movie);
                    realm.commitTransaction();
                });

        return movieAddObservable;
    }

    public void addMovieFromJsonArray(JSONArray jsonArray){
        Realm realm = Realm.getDefaultInstance();
        try{
            realm.beginTransaction();
            realm.createOrUpdateAllFromJson(Movie.class, jsonArray);
            realm.commitTransaction();
        }catch (Exception e){
            realm.cancelTransaction();
            throw e;
        }
    }

    public RealmResults<Movie> getAllMovies(){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Movie.class).findAll();
    }

    public RealmResults<Movie> getMovies(String name, long categoryId, Boolean haveSeen){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Movie> movieRealmQuery = realm.where(Movie.class);
        if(name != null && !name.equals("")){
            movieRealmQuery = movieRealmQuery.like("name", "*"+name+"*", Case.INSENSITIVE);
        }
        if(categoryId > 0){
            movieRealmQuery = movieRealmQuery.equalTo("category.id", categoryId);
        }

        if(haveSeen != null){
            movieRealmQuery = movieRealmQuery.equalTo("haveSeen", haveSeen);
        }

        return movieRealmQuery.findAll();
    }

    public Movie getMovie(long movieId){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Movie.class).equalTo("id", movieId).findFirst();
    }

    public Movie getFirstMovie(String name, long categoryId, Boolean haveSeen){
        return getMovies(name, categoryId, haveSeen).first();
    }

    public Movie getRandomMovie(String name, long categoryId, Boolean haveSeen){
        Random random = new Random();
        RealmResults<Movie> movieRealmResults = getMovies(name, categoryId, haveSeen);
        return movieRealmResults.get(random.nextInt(movieRealmResults.size()));
    }

    public void modifyMovieInfo(Movie movie, String name, Category category, boolean haveSeen, float starNum){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        movie.setName(name);
        movie.setCategory(category);
        movie.setHaveSeen(haveSeen);
        movie.setStarNum(starNum);
        movie.setModifyDt(new Date());
        realm.commitTransaction();
    }

    public void deleteMovie(Movie movie){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        movie.deleteFromRealm();
        realm.commitTransaction();
    }

    public void clearMovies(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Movie.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public Observable<Log> addLog(Log newLog){
        Realm realm = Realm.getDefaultInstance();
        Observable<Log>  logAddObservable = Observable.just(newLog)
                .map(log->{
                    log.setId(getNextKey(Log.class));
                    log.setLogDt(new Date());
                    return log;
                })
                .doOnNext(log -> {
                    realm.beginTransaction();
                    realm.copyToRealm(log);
                    realm.commitTransaction();
                });

        return logAddObservable;
    }

    public void addLogFromJsonArray(JSONArray jsonArray){
        Realm realm = Realm.getDefaultInstance();
        try{
            realm.beginTransaction();
            realm.createOrUpdateAllFromJson(Log.class, jsonArray);
            realm.commitTransaction();
        }catch (Exception e){
            realm.cancelTransaction();
            throw e;
        }
    }

    public Log getLastLog(long categoryId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Log> logRealmResults = realm.where(Log.class).equalTo("movie.category.id", categoryId).findAllSorted("logDt", Sort.DESCENDING);
        if(logRealmResults != null && logRealmResults.size() > 0){
            return logRealmResults.first();
        }
        return null;
    }

    public RealmResults<Log> getAllLogs(){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Log.class).findAllSorted("logDt", Sort.DESCENDING);
    }

    public void deleteLog(Log log){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        log.deleteFromRealm();
        realm.commitTransaction();
    }

    public void clearLogs(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(Log.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public long getNextKey(Class realmClass){
        Realm realm = Realm.getDefaultInstance();
        try {
            Number currentIdNum = realm.where(realmClass).max("id");
            long nextKey;
            if(currentIdNum == null){
                nextKey = 1;
            }else{
                nextKey = currentIdNum.longValue() + 1;
            }
            return nextKey;
        } catch (ArrayIndexOutOfBoundsException e)
        { return 1; }
    }

    public void backup(RealmResults realmResults, File file) throws Exception {
        Realm realm = Realm.getDefaultInstance();

        Writer writer = null;
        try {
            writer = new FileWriter(file);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            gson.toJson(realm.copyFromRealm(realmResults), writer);
        } catch (IOException e) {
            throw e;
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void restore(File file, Class clazz) throws Exception {
        if(!file.exists()) return;

        Realm realm = Realm.getDefaultInstance();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String s;
            while((s = br.readLine()) != null){
                sb.append(s);
            }
            String json = sb.toString();
            JSONArray jsonArray = new JSONArray(json);
            realm.beginTransaction();
            realm.createOrUpdateAllFromJson(clazz, jsonArray);
            realm.commitTransaction();
        } catch (FileNotFoundException e) {
            realm.cancelTransaction();
            throw e;
        } catch (IOException e) {
            realm.cancelTransaction();
            throw e;
        } catch (JSONException e) {
            realm.cancelTransaction();
            throw e;
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
