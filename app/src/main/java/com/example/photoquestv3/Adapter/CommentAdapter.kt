package com.example.photoquestv3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquestv3.Models.Comment
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CommentAdapter(
    private var commentList: List<Comment>,
    var currentUserData: User?,
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

    override fun getItemCount(): Int = commentList.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val commentItem = commentList[position]
        val userData = currentUserData

        val displayName = if (commentItem.userId == Firebase.auth.currentUser?.uid && userData != null) {
            userData.username
        } else {
            commentItem.username
        }
        holder.username.text = displayName
        holder.comment.text = commentItem.comment
        holder.heart.setImageResource(R.drawable.ic_heart)

        val profileUrl = if (commentItem.userId == Firebase.auth.currentUser?.uid && userData != null) {
            userData.imageUrl
        } else {
            commentItem.profilePicture
        }
        Glide.with(holder.itemView.context)
            .load(profileUrl)
            .placeholder(R.drawable.ic_person)
            .into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            val currentUser = Firebase.auth.currentUser?.uid ?: "No user here"
            if (commentItem.userId == currentUser) {
                onCommentClicked(commentItem)
            }
        }
    }

    /**
     * Returnerar kommentaren på given position.
     */
    fun getCommentAt(position: Int): Comment {
        return commentList[position]
    }

    /**
     * Uppdaterar listan med kommentarer och uppdaterar vyerna.
     */
    fun updateComments(newComments: List<Comment>) {
        commentList = newComments
        notifyDataSetChanged()
    }
}