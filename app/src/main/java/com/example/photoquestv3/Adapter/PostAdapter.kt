package com.example.photoquestv3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquestv3.Fragments.MoreOptionsPostBottomSheetFragment
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.ChallengesViewModel
import com.example.photoquestv3.ViewModel.PostViewModel

class PostAdapter(private val postList: List<Post>, val postVm : PostViewModel) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val imagePost: ImageView = itemView.findViewById(R.id.imagePost)
        val description: TextView = itemView.findViewById(R.id.description)
        val optionImage : ImageView = itemView.findViewById(R.id.moreOptions)

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


        holder.optionImage.setOnClickListener(){

            val postId = post.postId

            postVm.setItemId(postId)

            val moreOptionsFragment = MoreOptionsPostBottomSheetFragment()

            val activity = holder.itemView.context as? AppCompatActivity
            activity?.supportFragmentManager?.let {
                moreOptionsFragment.show(it, moreOptionsFragment.tag)
            }
        }

//        holder.profileImage.setImageResource(post.profilePic)

        Glide.with(holder.itemView.context)
            .load(post.profilePic)
            .placeholder(R.drawable.ic_person)
            .into(holder.profileImage)

        Glide.with(holder.itemView.context)
            .load(post.imageUrl)
            .into(holder.imagePost)

    }

}