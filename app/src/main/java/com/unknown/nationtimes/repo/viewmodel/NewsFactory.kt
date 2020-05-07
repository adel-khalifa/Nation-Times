package com.unknown.nationtimes.repo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unknown.nationtimes.repo.NewsRepo

class NewsFactory (
    private val mRepo : NewsRepo
): ViewModelProvider.Factory {
     // creating a viewModel factory which helps us to return an object
     // of ViewModel after passing their parameters to set later on to ViewModelProvider

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (NewsViewModel(mRepo)  as T)
    }
}