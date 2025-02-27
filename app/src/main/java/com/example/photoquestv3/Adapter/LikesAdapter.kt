package com.example.photoquestv3.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.model.content.RoundedCorners
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R

class LikesAdapter(
    private var friendList: MutableList<User>,
) : RecyclerView.Adapter<LikesAdapter.LikesViewHolder>() {

    inner class LikesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.textUserName)
        val profileImage: ImageView = itemView.findViewById(R.id.imageProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_likes, parent, false)
        return LikesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    override fun onBindViewHolder(holder: LikesViewHolder, position: Int) {
        val friendItem = friendList[position]

        holder.username.text = friendItem.username
        holder.profileImage.setImageResource(R.drawable.ic_person)

        Glide.with(holder.itemView.context)
            .load(friendItem.imageUrl)
            .placeholder(R.drawable.ic_person)
            .into(holder.profileImage)
    }


    fun updateList(friends: List<User>) {
        friendList.clear()
        friendList.addAll(friends)
        notifyDataSetChanged()
    }
}