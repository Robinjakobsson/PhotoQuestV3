package com.example.photoquestv3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquestv3.Models.Comment
import com.example.photoquestv3.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CommentAdapter(
    private var commentList: List<Comment>,
    private val onCommentClicked: (Comment) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

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
        val commentsItem = commentList[position]

        holder.username.text = commentsItem.username
        holder.comment.text = commentsItem.comment
        holder.heart.setImageResource(R.drawable.ic_heart)

        Glide.with(holder.itemView.context)
            .load(commentsItem.profilePicture)
            .placeholder(R.drawable.ic_person)
            .into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            val currentUser = Firebase.auth.currentUser?.uid ?: "No user here"
            if (commentsItem.userId == currentUser) {
                onCommentClicked(commentsItem)
            }
        }
    }

    fun getCommentAt(position: Int): Comment {
        return commentList[position]
    }

    fun updateComments(newComments: List<Comment>) {
        commentList = newComments
        notifyDataSetChanged()
    }
}