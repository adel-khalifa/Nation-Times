package com.unknown.nations.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.unknown.nations.R
import com.unknown.nations.adapter.ArticleAdapter
import com.unknown.nations.data.models.Article
import com.unknown.nations.repo.viewmodel.NewsViewModel
import com.unknown.nations.ui.HostActivity
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    private lateinit var viewModel: NewsViewModel
    private lateinit var articleAdapter: ArticleAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HostActivity).mNewsViewModel
        setUpRecyclerView()

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.DOWN or ItemTouchHelper.UP,
            ItemTouchHelper.END or ItemTouchHelper.START
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = articleAdapter.asyncListDiffer.currentList[position]

                viewModel.deleteThisArticle(article)
                Snackbar.make(view,"Deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo" , View.OnClickListener {
                        viewModel.insertOrUpdateThisArticle(article)
                    })
                    show()
                }
            }

        })
        touchHelper.attachToRecyclerView(rvSavedNews)

        viewModel.getAllSavedArticles().observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                Toast.makeText(context?.applicationContext, "Is Empty", Toast.LENGTH_LONG)
                    .show()
            } else {
                articleAdapter.asyncListDiffer.submitList(it)
                Toast.makeText(
                    context?.applicationContext,
                    "Is Not Empty and its size is ${it.size}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        // navigate to detailed fragment
        articleAdapter.setOnArticleClickListener(object :
            ArticleAdapter.OnArticleClickListener {
            override fun onArticleClick(position: Int, article: Article) {
                findNavController().navigate(
                    SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(article)
                )
            }
        })


    }

    private fun setUpRecyclerView() {

        articleAdapter = ArticleAdapter()
        rvSavedNews.apply {
            layoutManager = LinearLayoutManager(context.applicationContext)
            adapter = articleAdapter

        }
    }
}