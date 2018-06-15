package kr.co.ramza.moviemanager.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface NaverMovieSearchService{

    @GET("search/movie.json")
    fun search(@Query("query")  query:String,
               @Query("display")  display : Int,
               @Query("start")  start : Int) : Observable<Result>

    companion object Factory{
        private const val BASE_URL = "https://openapi.naver.com/v1/"

        fun create(): NaverMovieSearchService{
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor({
                chain ->
                val original = chain.request();
                val request = original.newBuilder()
                        .header("X-Naver-Client-Id", "qNATy9CR_E0jsBdkW7AN")
                        .header("X-Naver-Client-Secret", "Cn_NHtU1eP")
                        .method(original.method(), original.body())
                        .build();
                chain.proceed(request);
                })

            val client = httpClient.build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(client)
                    .build()
            return retrofit.create(NaverMovieSearchService::class.java)
        }
    }
}