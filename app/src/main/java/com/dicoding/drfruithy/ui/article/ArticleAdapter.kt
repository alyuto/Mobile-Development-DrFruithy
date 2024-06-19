package com.dicoding.drfruithy.ui.article

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.drfruithy.R
import com.dicoding.drfruithy.data.pref.ArticleDao
import com.dicoding.drfruithy.ui.articleDetail.DetailArticleActivity

class ArticleAdapter(private val article: ArrayList<ArticleDao>):
    RecyclerView.Adapter<ArticleAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val diseaseName: TextView = itemView.findViewById(R.id.diseaseName)
        val imageArticle: ImageView = itemView.findViewById(R.id.imageArticle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return article.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = article[position]
        holder.diseaseName.text = currentItem.nama_penyakit
        Glide.with(holder.itemView.context)
            .load(currentItem.image)
            .override(1100, 200)
            .into(holder.imageArticle)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailArticleActivity::class.java)
            intent.putExtra("article", currentItem)
            context.startActivity(intent)
        }
    }
}
