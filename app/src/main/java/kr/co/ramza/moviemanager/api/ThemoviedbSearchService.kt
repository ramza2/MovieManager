package kr.co.ramza.moviemanager.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface ThemoviedbSearchService{

    @GET("search/movie")
    fun search(@Query("api_key")  api_key:String,
               @Query("language")  language : String,
               @Query("query")  query : String,
               @Query("page")  page : Int,
               @Query("include_adult")  include_adult : String) : Observable<ThemoviedbResult>

    companion object Factory{
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun create(): ThemoviedbSearchService{
            val client = OkHttpClient.Builder().addInterceptor { chain ->
                val original = chain.request();
                val request = original.newBuilder()
                    .method(original.method(), original.body())
                    .build();
                chain.proceed(request);
            }.build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(client)
                    .build()
            return retrofit.create(ThemoviedbSearchService::class.java)
        }
    }
}