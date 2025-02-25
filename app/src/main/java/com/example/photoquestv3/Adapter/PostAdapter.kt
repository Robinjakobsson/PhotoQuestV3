package com.example.photoquestv3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.photoquestv3.Fragments.CommentFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Fragments.LikesFragment
import com.example.photoquestv3.Fragments.MoreOptionsPostBottomSheetFragment
import com.example.photoquestv3.Models.Comment
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.PostViewModel
import com.example.photoquestv3.Views.Fragments.ProfileFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter(
    private var postList: List<Post>,
    val postVm: PostViewModel,
    val onPostClicked: (Post) -> Unit,
    val onPostTextClicked: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {


    fun updatePosts(newPosts: List<Post>) {
        postList = newPosts
        notifyDataSetChanged()
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val imagePost: ImageView = itemView.findViewById(R.id.imagePost)
        val description: TextView = itemView.findViewById(R.id.description)
        val optionImage: ImageView = itemView.findViewById(R.id.moreOptions)
        val likeButton: ImageView = itemView.findViewById(R.id.likeIcon)
        var likeCounter: TextView = itemView.findViewById(R.id.likeCounter)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.userName.text = post.username
        holder.description.text = post.description

//        Likes
        holder.likeCounter.text = post.likes.toString()

        holder.likeCounter.setOnClickListener {

            val postId = post.postId
            val likesFragment = LikesFragment(postId)

            val activity = holder.itemView.context as? AppCompatActivity
            activity?.supportFragmentManager?.let {
                likesFragment.show(it, likesFragment.tag)
            }

        }

        holder.likeButton.setOnClickListener {
            postVm.addLikesToPost(post.postId)
        }


        holder.optionImage.setOnClickListener() {
            postVm.setItemId(post.postId)

            val moreOptionsFragment = MoreOptionsPostBottomSheetFragment()
            val activity = holder.itemView.context as? AppCompatActivity
            activity?.supportFragmentManager?.let {
                moreOptionsFragment.show(it, moreOptionsFragment.tag)
            }
        }

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

        holder.userName.setOnClickListener {
            onPostClicked(post)
        }
        holder.profileImage.setOnClickListener {
            onPostClicked(post)
        }
        holder.description.setOnClickListener {
            val currentUser = Firebase.auth.currentUser?.uid ?: "No user here"
            if (post.userid == currentUser) {
                onPostTextClicked(post)
            }
        }

    }
}