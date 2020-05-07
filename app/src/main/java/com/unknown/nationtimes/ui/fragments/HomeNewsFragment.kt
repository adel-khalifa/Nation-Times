package com.unknown.nationtimes.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.unknown.nationtimes.R
import com.unknown.nationtimes.adapter.ArticleAdapter
import com.unknown.nationtimes.data.models.Article
import com.unknown.nationtimes.data.models.NewsDataResponse
import com.unknown.nationtimes.network.NetworkState
import com.unknown.nationtimes.repo.viewmodel.NewsViewModel
import com.unknown.nationtimes.ui.HostActivity
import kotlinx.android.synthetic.main.fragment_home_news.*

class HomeNewsFragment : Fragment(R.layout.fragment_home_news),
    ArticleAdapter.OnArticleClickListener {
    private lateinit var adapter: ArticleAdapter
    private lateinit var mViewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = (activity as HostActivity).mNewsViewModel
        setupRecyclerViewWithAdapter()

        val topHeadLineHashMap: HashMap<String, String> = HashMap()
        topHeadLineHashMap["country"] = "eg"
        //TODO update hashMap data with many queries


        // manage Api interface by passing their queries parameters
        mViewModel.getTopHeadLinesNews(topHeadLineHashMap)

        mViewModel.topHeadLinesLiveData.observe(viewLifecycleOwner, Observer { dataAndStateResponse ->
            //handling views depends on network state
            when (dataAndStateResponse) {
                is NetworkState.OnLoading -> setViewToBaseState()
                is NetworkState.OnSuccess -> {  setViewToSuccessState()
                    adapter.asyncListDiffer.submitList(dataAndStateResponse.bodyData?.articles)
                }
                is NetworkState.OnFailure -> setViewToFailureState(dataAndStateResponse)
            }

            Toast.makeText(
                context?.applicationContext,
                "  ${dataAndStateResponse.bodyData?.status} \n " +
                        "content size = ${dataAndStateResponse.bodyData?.totalResults} ",
                Toast.LENGTH_LONG
            ).show()
        })
        adapter.setOnArticleClickListener(this)
    }

    private fun setupRecyclerViewWithAdapter() {
        adapter = ArticleAdapter()
        home_recycler.layoutManager = LinearLayoutManager(context)
        home_recycler.adapter = adapter

    }


    private fun setViewToBaseState() {
        home_progressBar.visibility = ProgressBar.VISIBLE
        home_tv_state.visibility = TextView.VISIBLE
        home_tv_state.text = getString(R.string.onLoadingState)
    }

    private fun setViewToFailureState(dataAndStateResponse: NetworkState<NewsDataResponse>) {
        home_progressBar.visibility = ProgressBar.INVISIBLE
        home_tv_state.apply {
            this.visibility = TextView.VISIBLE
            this.text = dataAndStateResponse.failureMessage
        }
    }


    private fun setViewToSuccessState() {
        home_progressBar.visibility = ProgressBar.INVISIBLE
        home_tv_state.visibility = TextView.INVISIBLE
    }


    override fun onArticleClick(position: Int, article: Article) {
        Toast.makeText(this.requireContext(), "$position : " + article.title, Toast.LENGTH_LONG)
            .show()
    }

}