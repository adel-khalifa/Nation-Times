package com.unknown.nations.repo.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.unknown.nations.R
import com.unknown.nations.application.NewsApplication
import com.unknown.nations.data.models.Article
import com.unknown.nations.data.models.NewsDataResponse
import com.unknown.nations.network.NetworkState
import com.unknown.nations.repo.NewsRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(val app: Application, private val mRepo: NewsRepo) : AndroidViewModel(app) {


    private var thPagingResponse: Response<NewsDataResponse>? = null
    private var pageCountForTopHeadLines = 0
    private var reload: Boolean? = null
    private var reloadSearch: Boolean? = null


    // holder network state updates
    var topHeadLinesLiveData: MutableLiveData<NetworkState<NewsDataResponse>> =
        MutableLiveData()

    private var allNewsPagingResponse: Response<NewsDataResponse>? = null
    private var pageCountForAllNews = 0

    // holder network state updates
    var allNewsLiveData: MutableLiveData<NetworkState<NewsDataResponse>> =
        MutableLiveData()

    fun getAllNews(queryHashMapForAllNews: HashMap<String, String>, isNewSearch: Boolean) {
        reloadSearch = isNewSearch
        if(isNewSearch){
            pageCountForAllNews=0
        }
        viewModelScope.launch {
            if (pageCountForAllNews <= 5) {
                pageCountForAllNews++
            }
            queryHashMapForAllNews["page"] = "$pageCountForAllNews"
            makeSafeRequestForAllNews(queryHashMapForAllNews)
        }
    }

    fun getTopHeadLinesNews(
        queryHashMapForTopHeadLines: HashMap<String, String>,
        isNewCategory: Boolean,
        resetPage: Boolean
    ) {
        reload = isNewCategory

        // call suspend function within viewModelScope
        viewModelScope.launch {
            if (resetPage) {
                pageCountForTopHeadLines = 1
            }
            if (pageCountForTopHeadLines < 5 && !isNewCategory) {
                pageCountForTopHeadLines++
            }
            queryHashMapForTopHeadLines["page"] = "$pageCountForTopHeadLines"
            makeSafeRequestForTopHeadLinesNews(queryHashMapForTopHeadLines)
        }
    }


    private suspend fun makeSafeRequestForAllNews(queryHashMap: HashMap<String, String>) {
        allNewsLiveData.postValue(NetworkState.OnLoading())
        delay(1000L)
        if (checkInternetConnection()) {
            try {
                // set whenever state depends on response state
                val allNewsResponse = mRepo.getEverythingFromApi(queryHashMap)
                increasePagesForAllNews(allNewsResponse)
delay(500L)
                // set whenever state depends on response state

                if (allNewsResponse.isSuccessful) {
                    setAllNewsToSuccess(allNewsPagingResponse ?: allNewsResponse)
                } else {
                    setAllNewsToFailure(
                        allNewsPagingResponse?.message() ?: allNewsResponse.message()
                    )
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> setAllNewsToFailure(
                        getApplication<NewsApplication>().getString(R.string.network_failure)
                    )
                }
            }
        } else {
            setAllNewsToFailure(getApplication<NewsApplication>().getString(R.string.no_internet_connection))
        }

    }

    private suspend fun makeSafeRequestForTopHeadLinesNews(
        queryHashMap: HashMap<String, String>
    ) {
        topHeadLinesLiveData.postValue(NetworkState.OnLoading())
        delay(500L)
        if (checkInternetConnection()) {
            try {
                val topHeadLinesResponse = mRepo.getTopHeadLinesFromApi(queryHashMap)

                increasePagesForTopNews(topHeadLinesResponse)

                // set whenever state depends on response state
                if (topHeadLinesResponse.isSuccessful) {
                    setTopHeadsNewsToSuccess(thPagingResponse ?: topHeadLinesResponse)
                } else {
                    setTopHeadsNewsToFailure(
                        thPagingResponse?.message()
                            ?: topHeadLinesResponse.message()
                    )
                }

            } catch (t: Throwable) {
                when (t) {
                    is IOException -> setTopHeadsNewsToFailure(
                        getApplication<NewsApplication>()
                            .getString(R.string.network_failure)
                    )
                }
            }
        } else {
            setTopHeadsNewsToFailure(getApplication<NewsApplication>().getString(R.string.no_internet_connection))
        }
    }

    private fun increasePagesForAllNews(allNewsResponse: Response<NewsDataResponse>) {
        if (allNewsPagingResponse == null) {
            allNewsPagingResponse = allNewsResponse
        } else {
            val oldData = allNewsPagingResponse?.body()?.articles
            if(reloadSearch!!){
                oldData?.clear()
            }
            val newData = allNewsResponse.body()?.articles

            newData?.let { oldData?.addAll(it) }

        }
    }

    private fun increasePagesForTopNews(topHeadLinesResponse: Response<NewsDataResponse>) {
        if (thPagingResponse == null) {
            thPagingResponse = topHeadLinesResponse
        } else {

            val oldData = thPagingResponse?.body()?.articles
            if (reload!!) {
                oldData?.clear()
            }
            val newData = topHeadLinesResponse.body()?.articles

            newData?.let { oldData?.addAll(it) }


        }
    }


    fun insertOrUpdateThisArticle(article: Article) =
        viewModelScope.launch { mRepo.updateOrInsertArticle(article) }

    fun deleteThisArticle(article: Article) =
        viewModelScope.launch { mRepo.deleteArticle(article) }

    fun getAllSavedArticles() = mRepo.getAllArticles()

    fun lookForThisArticle(articleUrl: String) = mRepo.lookForThisArticle(articleUrl)


    // setting state of response to handling actions in our components depending on that state
    private fun setTopHeadsNewsToSuccess(responseX: Response<NewsDataResponse>) =
        responseX.body()?.let {response->
            topHeadLinesLiveData.postValue(NetworkState.OnSuccess(responseBody = response))
        }

    private fun setTopHeadsNewsToFailure(failureMessage: String) =
        topHeadLinesLiveData.postValue(NetworkState.OnFailure(failureMessage))


    private fun setAllNewsToSuccess(responseX: Response<NewsDataResponse>) =
        responseX.body()?.let {response ->
            allNewsLiveData.postValue(NetworkState.OnSuccess(responseBody = response))
        }

    private fun setAllNewsToFailure(failureMessage: String) =
        allNewsLiveData.postValue(NetworkState.OnFailure(failureMessage))


    private fun checkInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}