package com.example.photoquestv3.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R
import com.example.photoquestv3.Views.Fragments.LoginFragment
import com.example.photoquestv3.Views.Fragments.ProfileFragment

class SearchResultsAdapter(
    val context: Context,
    val matchingUsers: MutableList<User>,
) :
    RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView =
            itemView.findViewById(R.id.userNameTextView)
        val userImage: ImageView = itemView.findViewById(R.id.userImageView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.found_user_item,
            parent,
            false
        )

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chosenUser = matchingUsers[position]
        holder.userName.text = matchingUsers[position].username
        holder.itemView.setOnClickListener {
            //TODO add something that will open a profile fragment
        }
        Glide.with(context)
            .load(matchingUsers[position].imageUrl)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(holder.userImage)

    }

    override fun getItemCount(): Int {
        return matchingUsers.size
    }

    fun updateData(newList : List<User>) {
        matchingUsers.clear()
        matchingUsers.addAll(newList)
        notifyDataSetChanged()
    }


}
