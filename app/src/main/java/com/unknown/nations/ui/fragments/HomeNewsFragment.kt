package com.unknown.nations.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.unknown.nations.R
import com.unknown.nations.adapter.ArticleAdapter
import com.unknown.nations.data.models.Article
import com.unknown.nations.data.models.NewsDataResponse
import com.unknown.nations.network.NetworkState
import com.unknown.nations.repo.viewmodel.NewsViewModel
import com.unknown.nations.ui.HostActivity
import kotlinx.android.synthetic.main.fragment_home_news.*
import kotlin.collections.HashMap

class HomeNewsFragment : Fragment(R.layout.fragment_home_news),
    ArticleAdapter.OnArticleClickListener {

    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var mViewModel: NewsViewModel
    val topHeadLineHashMap: HashMap<String, String> = HashMap()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = (activity as HostActivity).mNewsViewModel
        setupRecyclerViewWithAdapter()
////////////////////////////////////////////////////////////////////////////////////////////////
        topHeadLineHashMap["country"] = "eg"
        topHeadLineHashMap["category"] = "entertainment"
        //TODO update hashMap data with many queries
////////////////////////////////////////////////////////////////////////////////////////////////
        // initial request
        home_sports_fab_.setOnClickListener {

            topHeadLineHashMap["category"] = "sports"
            mViewModel.getTopHeadLinesNews(topHeadLineHashMap, true, resetPage = true)


        }

        home_technology_fab.setOnClickListener {

            topHeadLineHashMap["category"] = "technology"
            mViewModel.getTopHeadLinesNews(topHeadLineHashMap, true, resetPage = true)

        }
        mViewModel.getTopHeadLinesNews(topHeadLineHashMap, isNewCategory = true, resetPage = true)


        // observe the result request with its Network State
        mViewModel.topHeadLinesLiveData.observe(
            viewLifecycleOwner,
            Observer { dataAndStateResponse ->
                //handling views depends on network state
                when (dataAndStateResponse) {
                    is NetworkState.OnLoading -> setViewToBaseState()
                    is NetworkState.OnSuccess -> updateViewAndAdapterData(dataAndStateResponse)
                    is NetworkState.OnFailure -> setViewToFailureState(dataAndStateResponse)
                }
                Toast.makeText(
                    context?.applicationContext,
                    "  ${dataAndStateResponse.bodyData?.status} \n " +
                            "content size = ${dataAndStateResponse.bodyData?.totalResults} " +
                            "+\n" +
                            "\"adapter size = ${articleAdapter.asyncListDiffer.currentList.size}"
                    ,
                    Toast.LENGTH_LONG
                ).show()


            })
        articleAdapter.setOnArticleClickListener(this)


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

            val scrolledBitDown = firstShownArticlePosition > 0
            val atBottomOfRecycler =
                sumOfVisibleArticlePositions + firstShownArticlePosition >= allArticlesInRecycler

            if (scrolledBitDown && atBottomOfRecycler && !isLastArticleReached && !isLoading) {
                mViewModel.getTopHeadLinesNews(topHeadLineHashMap, false, resetPage = false)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

        }

    }

    private fun setupRecyclerViewWithAdapter() {
        articleAdapter = ArticleAdapter()
        home_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
            addOnScrollListener(sListener)
        }
    }


    private fun setViewToBaseState() {
        home_progressBar.visibility = ProgressBar.VISIBLE
        home_tv_state.visibility = TextView.VISIBLE
        home_tv_state.text = getString(R.string.onLoadingState)
        isLoading = true
    }

    private fun setViewToFailureState(dataAndStateResponse: NetworkState<NewsDataResponse>) {
        home_progressBar.visibility = ProgressBar.INVISIBLE
        home_tv_state.apply {
            this.visibility = TextView.VISIBLE
            this.text = dataAndStateResponse.failureMessage
        }
    }


    private fun updateViewAndAdapterData(newResponseData: NetworkState<NewsDataResponse>) {

        home_progressBar.visibility = ProgressBar.INVISIBLE
        home_tv_state.visibility = TextView.INVISIBLE
        articleAdapter.asyncListDiffer.submitList(newResponseData.bodyData?.articles?.toList())
        isLoading = false

        isLastArticleReached =
            articleAdapter.asyncListDiffer.currentList.size == newResponseData.bodyData?.totalResults

        if (isLastArticleReached) {
            home_recycler.setPadding(0, 0, 0, 0)
        }


    }


    override fun onArticleClick(position: Int, article: Article) {
        Toast.makeText(this.requireContext(), "$position : " + article.title, Toast.LENGTH_LONG)
            .show()
        findNavController().navigate(HomeNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(article))

    }

}