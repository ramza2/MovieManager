package kr.co.ramza.moviemanager.di.module

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import kr.co.ramza.moviemanager.di.annotation.PerActivity
import kr.co.ramza.moviemanager.ui.view.MovieSearchViewModel

@Module
class ViewModelModule(private val activity: AppCompatActivity){

    @PerActivity
    @Provides
    fun provideMovieSearchViewModel() = MovieSearchViewModel.create(activity)
}