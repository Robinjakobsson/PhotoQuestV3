package com.example.photoquestv3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Models.Comment
import com.example.photoquestv3.R

class CommentAdapter(private var commentList: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val username: TextView = itemView.findViewById(R.id.textUserName)
        val comment: TextView = itemView.findViewById(R.id.textComment)
        val heart: ImageView = itemView.findViewById(R.id.icon_heart)
        val imageProfile: ImageView = itemView.findViewById(R.id.imageProfile)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comments = commentList[position]

        holder.username.text = comments.username
        holder.comment.text = comments.comment
        holder.heart.setImageResource(R.drawable.ic_heart)
        holder.imageProfile.setImageResource(R.drawable.ic_person)
    }

    fun updateComments(newComments: List<Comment>) {
        commentList = newComments
        notifyDataSetChanged()
    }
}