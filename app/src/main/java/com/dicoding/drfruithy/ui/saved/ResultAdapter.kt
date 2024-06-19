package com.dicoding.drfruithy.ui.saved

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.drfruithy.R
import com.dicoding.drfruithy.data.pref.ResultItem
import com.dicoding.drfruithy.ui.articleDetail.DetailArticleActivity
import com.google.firebase.storage.FirebaseStorage

class ResultAdapter(private val saved: ArrayList<ResultItem>, private val listener: OnItemDeleteListener) : RecyclerView.Adapter<ResultAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_saved, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return saved.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = saved[position]
        holder.diseaseName.text = currentItem.diseaseName
        holder.titleSaved.text = currentItem.title

        // Load image using Glide or another image loading library
        val storageRef = FirebaseStorage.getInstance("gs://dbtest-a7bc7.appspot.com").reference
        val imageRef = storageRef.child("images/${currentItem.title}.jpg")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(holder.itemView.context)
                .load(uri)
                .placeholder(R.drawable.home_tree) // Placeholder image
                .error(R.drawable.earth) // Error image
                .into(holder.imageSaved)
        }.addOnFailureListener { e ->
            Log.e("FirebaseStorage", "Error loading image for ${currentItem.title}: ${e.message}")
            // Handle error loading image
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, SavedDetailActivity::class.java)
            intent.putExtra("RESULT_ITEM", currentItem)
            context.startActivity(intent)
        }

        holder.buttonDelete.setOnClickListener {
            listener.onItemDelete(currentItem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleSaved: TextView = itemView.findViewById(R.id.title_saved_fill)
        val imageSaved: ImageView = itemView.findViewById(R.id.image_saved_card)
        val diseaseName: TextView = itemView.findViewById(R.id.disease_saved_fill)
        val buttonDelete: Button = itemView.findViewById(R.id.button_delete)
    }

    interface OnItemDeleteListener {
        fun onItemDelete(item: ResultItem)
    }
}