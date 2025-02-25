package com.example.photoquestv3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquestv3.R

class ProfileAdapter(var imageUrl: List<String>) : RecyclerView.Adapter<ProfileAdapter.ImageViewHolder> (){

    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView = itemView.findViewById(R.id.profile_holder)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profileholder,parent,false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int = imageUrl.size



    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imageUrl[position])
            .centerCrop()
            .into(holder.imageView)
    }

    fun updateData(newImages: List<String>) {
        imageUrl = newImages
        notifyDataSetChanged()
    }
}