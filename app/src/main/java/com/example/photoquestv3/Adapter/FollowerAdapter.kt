package com.example.photoquestv3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R

class FollowerAdapter(
    val followers : MutableList<User>,
    val onFollowerClicked : (User) -> Unit
) : RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder>() {

    inner class FollowerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val username : TextView = itemView.findViewById(R.id.textUserName)
        val profileImage : ImageView = itemView.findViewById(R.id.imageProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_likes, parent,false)
        return FollowerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return followers.size
    }

    override fun onBindViewHolder(holder: FollowerViewHolder, position: Int) {
        val follower = followers[position]

        holder.username.text = follower.username

        Glide.with(holder.itemView.context)
            .load(follower.imageUrl)
            .placeholder(R.drawable.ic_person)
            .into(holder.profileImage)

        holder.profileImage.setOnClickListener {
            onFollowerClicked(follower)
        }
        holder.username.setOnClickListener {
            onFollowerClicked(follower)
        }
    }

    fun updateData(newList : List<User>) {
        followers.clear()
        followers.addAll(newList)
        notifyDataSetChanged()
    }
}