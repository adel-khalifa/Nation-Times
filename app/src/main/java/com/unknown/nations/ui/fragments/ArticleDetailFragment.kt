package com.unknown.nations.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.unknown.nations.R
import com.unknown.nations.data.models.Article
import com.unknown.nations.repo.viewmodel.NewsViewModel
import com.unknown.nations.ui.HostActivity
import kotlinx.android.synthetic.main.fragment_article_detail.*

class ArticleDetailFragment : Fragment(R.layout.fragment_article_detail) {
    private val args: ArticleDetailFragmentArgs by navArgs()
    private lateinit var viewModel: NewsViewModel
    private lateinit var article: Article

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as HostActivity).mNewsViewModel
        article = args.comingArticle
        viewModel.lookForThisArticle(article.url!!).observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                fab.apply {
                    hide()
                }
            }
        })

        webView.apply {
            // as explicit intent , to make sure this webView is the main host of article url
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
        fab.setOnClickListener {
            viewModel.insertOrUpdateThisArticle(article)

            Snackbar.make(view, R.string.article_has_been_saved, Snackbar.LENGTH_LONG).apply {
                Snackbar.make(view,"Deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo", View.OnClickListener {
                        viewModel.deleteThisArticle(article)
                    })
                    show()
                }
            }

        }

    }

}

