package com.unknown.nationtimes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unknown.nationtimes.R
import com.unknown.nationtimes.data.models.Article
import kotlinx.android.synthetic.main.item_article_element.view.*

class ArticleAdapter(


) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private lateinit var listener: OnArticleClickListener

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    //      implementing a ItemCallBack interface to used by DiffUtil when needed
    //     to change a specific element in the desire list without replacing a whole list
    class DiffImpl : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    private val diffCallBack = DiffImpl()
    // List of Data place holder / the object which can access from outside the adapter to add/update data
    val asyncListDiffer = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article_element, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }


    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        asyncListDiffer.currentList.let {
            val article = it[position]
            holder.itemView.apply {
                Glide.with(this).load(article.urlToImage).into(ivArticleImage)
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt
                tvSource.text = article.source.name
                tvTitle.text = article.title
                // passing data outside the Adapter to handling desire clicked item
                setOnClickListener {
                    listener.onArticleClick(position, article)
                }

            }
        }
    }


    interface OnArticleClickListener {
        fun onArticleClick(position: Int, article: Article)
    }

    fun setOnArticleClickListener(onArticleClickListener: OnArticleClickListener) {
        listener = onArticleClickListener
    }


}
