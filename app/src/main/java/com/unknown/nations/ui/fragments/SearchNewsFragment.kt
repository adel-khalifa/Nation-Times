package com.unknown.nations.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unknown.nations.R
import com.unknown.nations.adapter.ArticleAdapter
import com.unknown.nations.data.models.Article
import com.unknown.nations.data.models.NewsDataResponse
import com.unknown.nations.network.NetworkState
import com.unknown.nations.repo.viewmodel.NewsViewModel
import com.unknown.nations.ui.HostActivity
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news),
    ArticleAdapter.OnArticleClickListener {


    private lateinit var searchAdapter: ArticleAdapter
    private lateinit var viewModel: NewsViewModel

    // hashMap which used by retrofit as queryHashMap
    private val everythingEndPointHashMap: HashMap<String, String> = HashMap()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as HostActivity).mNewsViewModel
        setupRecyclerViewWithAdapter()

        var job: Job? = null
        search_et.addTextChangedListener {
            // when text is changing ( user typing) cancel the current coroutines
            // to avoid multi-requests per second (delay-time)
            val textForQuery = search_et.text.toString()
            job?.cancel()
            //  Log.d("QUERYTEST" , " after job $textForQuery ")
            if (textForQuery.isNotEmpty() && textForQuery.isNotBlank() && !textForQuery.isDigitsOnly()) {
                // start coroutine to hit the server and initialize the control point ( our job instance )
                job = MainScope().launch {
                    // delay to reduce server-requests load
                    delay(1000L)
                    // used to make request and update the live data which is observable
                    //     Log.d("QUERYTEST" , " before request $textForQuery ")

                    viewModel.getAllNews(setupQueryHashMap(textForQuery), isNewSearch = true)
                }
                // set view depends on response state
                viewModel.allNewsLiveData.observe(
                    viewLifecycleOwner,
                    Observer { searchResultResponse ->
                        when (searchResultResponse) {
                            is NetworkState.OnLoading -> {
                                setViewToLoadState()
                                searchResultResponse.bodyData?.articles?.clear()
                            }
                            is NetworkState.OnSuccess -> {
                                setViewToSuccessState(searchResultResponse)
                                isLastArticleReached =
                                    ((searchAdapter.asyncListDiffer.currentList.size >= 100) or
                                            (searchAdapter.asyncListDiffer.currentList.size == searchResultResponse.bodyData?.articles?.size))
                                isLoading = false
                                if (isLastArticleReached) {
                                    search_recycler.setPadding(0, 0, 0, 0)
                                }
                                // pass articles to list-differ which within the adapter to update news recyclerView
                                searchAdapter.asyncListDiffer.submitList(searchResultResponse.bodyData?.articles?.toList())
                            }
                            is NetworkState.OnFailure -> setViewToFailureState(searchResultResponse)
                        }
                        Toast.makeText(
                            context?.applicationContext,
                            "  ${searchResultResponse.bodyData?.status} \n  " +
                                    "content size = ${searchResultResponse.bodyData?.totalResults} ",
                            Toast.LENGTH_LONG
                        ).show()
                    })
            } else {
                // clear recyclerView Elements
                searchAdapter.asyncListDiffer.submitList(null)
            }
            searchAdapter.setOnArticleClickListener(this)
        }

    }


    private fun setupQueryHashMap(textForQuery: String): HashMap<String, String> {
        // setup the base elements according to Api required query parameters

        everythingEndPointHashMap["qInTitle"] = ""
        everythingEndPointHashMap["language"] = "ar"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            everythingEndPointHashMap.replace("qInTitle", textForQuery)
        } else {
            everythingEndPointHashMap["qInTitle"] = textForQuery
        }
        return everythingEndPointHashMap
    }

    private var isLastArticleReached = false
    private var isLoading = false

    private val sListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val lm = recyclerView.layoutManager as LinearLayoutManager

            val firstShownArticlePosition = lm.findFirstVisibleItemPosition()
            val sumOfVisibleArticlePositions = lm.childCount
            val allArticlesInRecycler = lm.itemCount
            // make sure
            val scrolledBitDown = firstShownArticlePosition > 0
            val atBottomOfRecycler =
                sumOfVisibleArticlePositions + firstShownArticlePosition >= allArticlesInRecycler
            //val requestHasMoreThanPageContent = allArticlesInRecycler >= 20
            if (scrolledBitDown && atBottomOfRecycler && !isLastArticleReached && !isLoading) {
                viewModel.getAllNews(
                    setupQueryHashMap(search_et.text.toString()),
                    isNewSearch = false
                )
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

        }

    }


    override fun onArticleClick(position: Int, article: Article) {
        Toast.makeText(context, "${article.source?.name}  ", Toast.LENGTH_SHORT).show()
        findNavController().navigate(
            SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(
                article
            )
        )
    }

    private fun setupRecyclerViewWithAdapter() {
        searchAdapter = ArticleAdapter()
        search_recycler.layoutManager = LinearLayoutManager(context)
        search_recycler.adapter = searchAdapter
        search_recycler.addOnScrollListener(sListener)

    }


    private fun setViewToLoadState() {
        search_progressBar.visibility = ProgressBar.VISIBLE
        search_tv_state.visibility = TextView.INVISIBLE
        search_tv_state.text = getString(R.string.no_result_found)
        isLoading = true
    }

    private fun setViewToFailureState(dataAndStateResponse: NetworkState<NewsDataResponse>) {
        search_progressBar.visibility = ProgressBar.INVISIBLE
        search_tv_state.apply {
            this.visibility = TextView.VISIBLE
            this.text = dataAndStateResponse.failureMessage
        }
    }


    private fun setViewToSuccessState(dataAndStateResponse: NetworkState<NewsDataResponse>) {
        search_progressBar.visibility = ProgressBar.INVISIBLE
        search_tv_state.visibility = TextView.INVISIBLE

        if (dataAndStateResponse.bodyData?.articles?.size == 0) {

            search_tv_state.apply {
                text = getString(R.string.no_result_found)
                visibility = TextView.VISIBLE
            }
        }
    }

}