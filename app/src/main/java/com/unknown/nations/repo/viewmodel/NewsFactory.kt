package com.unknown.nations.repo.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unknown.nations.repo.NewsRepo

class NewsFactory (
    val app : Application ,
    private val mRepo : NewsRepo
): ViewModelProvider.Factory {
     // creating a viewModel factory which helps us to return an object
     // of ViewModel after passing their parameters to set later on to ViewModelProvider

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (NewsViewModel(app , mRepo)  as T)
    }
}