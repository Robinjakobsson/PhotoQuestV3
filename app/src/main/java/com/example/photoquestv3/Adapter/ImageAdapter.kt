package com.example.photoquestv3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.R

class ImageAdapter(private val images: List<Int>):
RecyclerView.Adapter<ImageAdapter.ViewHolder>(){

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val images: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = images.size



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.images.setImageResource(images[position])
    }
}