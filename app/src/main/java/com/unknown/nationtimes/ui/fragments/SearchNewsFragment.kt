package com.unknown.nationtimes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unknown.nationtimes.R
import com.unknown.nationtimes.network.RetrofitInstance
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.view.*
import kotlinx.android.synthetic.main.fragment_search_news.view.etSearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val everythingEndPointHashMap : HashMap<String , String> =HashMap()
        everythingEndPointHashMap["q"] = "android"
        everythingEndPointHashMap["page"] = "1"
        everythingEndPointHashMap["language"]= "en"

    }
}