package com.unknown.nationtimes.repo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unknown.nationtimes.data.models.NewsDataResponse
import com.unknown.nationtimes.network.NetworkState
import com.unknown.nationtimes.repo.NewsRepo
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    private val mRepo: NewsRepo
) : ViewModel() {
    private val pageCountForTopHeadLines = "2"

    // holder network states updates
    var topHeadLinesLiveData: MutableLiveData<NetworkState<NewsDataResponse>> =
        MutableLiveData()
  

    fun getTopHeadLinesNews(queryHashMapForTopHeadLines: HashMap<String, String>) =
        // call suspend function within viewModelScope
        viewModelScope.launch {
            // set initial state on loading
            topHeadLinesLiveData.postValue(NetworkState.OnLoading())
            val topHeadLinesResponse = mRepo.getTopHeadLinesFromApi(queryHashMapForTopHeadLines)
            // set whenever state depends on response state
            if (topHeadLinesResponse.isSuccessful) setTopHeadsNewsToSuccess(topHeadLinesResponse)
            else setTopHeadsNewsToFailure(topHeadLinesResponse)
        }




    // setting state of response to handling actions in our components depending on that state
    private fun setTopHeadsNewsToSuccess(responseX: Response<NewsDataResponse>) =
        responseX.body()?.let {
            topHeadLinesLiveData.postValue(NetworkState.OnSuccess(responseBody = it))

        }

    private fun setTopHeadsNewsToFailure(responseX: Response<NewsDataResponse>) =
        responseX.let {
            topHeadLinesLiveData.postValue(NetworkState.OnFailure(it.message()))


        }

}