package com.example.photoquestv3.Adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.photoquestv3.Fragments.CommentFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Fragments.HomeFragment
import com.example.photoquestv3.Fragments.MoreOptionsPostBottomSheetFragment
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.ChallengesViewModel
import com.example.photoquestv3.ViewModel.PostViewModel
import com.example.photoquestv3.Views.Fragments.ProfileFragment

class PostAdapter(
    private val fragment: Fragment,
    private var postList: List<Post>,
    val postVm : PostViewModel,
    val matchingUsers: MutableList<User>,
    val onUserClicked: (User) -> Unit
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

        holder.optionImage.setOnClickListener() {

            val postId = post.postId

            postVm.setItemId(postId)

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

        holder.userName.setOnClickListener{
            Log.d("###", "aaa")
            if (fragment is HomeFragment) {
                fragment.navigateToProfile()
            }
        }

        holder.profileImage.setOnClickListener {
            Log.d("###", "bbb")
            if (fragment is HomeFragment) {
                fragment.navigateToProfile()
            }

        }

    }
}