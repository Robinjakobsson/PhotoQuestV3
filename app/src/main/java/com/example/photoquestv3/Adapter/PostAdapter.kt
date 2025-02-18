package com.example.photoquestv3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquestv3.Fragments.CommentFragment
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.R

class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val imagePost: ImageView = itemView.findViewById(R.id.imagePost)
        val description: TextView = itemView.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
       return  postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]

        holder.userName.text = post.username
        holder.description.text = post.description


        Glide.with(holder.itemView.context)
            .load(post.profilePic)
            .placeholder(R.drawable.ic_person)
            .into(holder.profileImage)

        Glide.with(holder.itemView.context)
            .load(post.imageUrl)
            .into(holder.imagePost)

        holder.itemView.findViewById<ImageView>(R.id.addComment).setOnClickListener {

            val bottomSheet = CommentFragment(post.postId)
            (holder.itemView.context as AppCompatActivity).supportFragmentManager.let { fm ->
                bottomSheet.show(fm, "CommentBottomSheet")
            }
        }

    }

}