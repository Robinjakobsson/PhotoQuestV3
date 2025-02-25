package com.example.photoquestv3.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.Models.Comment
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R

class LikesAdapter (

    private val context : Context,
    private var friendList: List<User>,
    ) : RecyclerView.Adapter<LikesAdapter.LikesViewHolder>() {

    inner class LikesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.userNameTextViewLikes)
        var profileImage : ImageView = itemView.findViewById(R.id.ProfileImageLikes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_likes, parent, false)
        return LikesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    override fun onBindViewHolder(holder: LikesViewHolder, position: Int) {
        val friend = friendList[position]

        holder.username.text = friend.username

        Glide.with(holder.itemView.context)
            .load(friend.imageUrl)
            .placeholder(R.drawable.ic_person)
            .into(holder.profileImage)
    }

    
    fun updateList(friends : List<User>) {
        friendList = friends
        Log.d("!!!", "update friendlist. ${friendList.size}")
        notifyDataSetChanged()

    }
}