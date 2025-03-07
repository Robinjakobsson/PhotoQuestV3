package com.example.photoquestv3.Adapter


import android.animation.Animator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoquestv3.Fragments.CommentFragment
import com.example.photoquestv3.Fragments.LikesFragment
import com.example.photoquestv3.Fragments.MoreOptionsPostBottomSheetFragment
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.PostViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter(
    private var postList: List<Post>,
    val postVm: PostViewModel,
    private val currentUserId: String,
    var currentUserProfileUrl: String?,
    val onPostClicked: (Post) -> Unit,
    val currentUserData: User?,
    val onPostTextClicked: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    //  Shimmer check
    var isShimmer= true

    fun updatePosts(newPosts: List<Post>) {
        isShimmer = false
        postList = newPosts
        notifyDataSetChanged()
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val imagePost: ImageView = itemView.findViewById(R.id.imagePost)
        val description: TextView = itemView.findViewById(R.id.description)
        val optionImage: ImageView = itemView.findViewById(R.id.moreOptions)
        val likeButton : ImageView = itemView.findViewById(R.id.likeIcon)
        var likeCounter : TextView = itemView.findViewById(R.id.likeCounter)
        val cardView : CardView = itemView.findViewById(R.id.itemCardView)
        val heartAnim: com.airbnb.lottie.LottieAnimationView = itemView.findViewById(R.id.heartAnim)
        val shimmerFrameLayout: ShimmerFrameLayout = itemView.findViewById(R.id.shimmerFrame)
        val feedContent:  View = itemView.findViewById(R.id.feedContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (isShimmer) 5 else postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        //  Shimmer effect on cardView
        if (isShimmer) {
            holder.shimmerFrameLayout.visibility
            holder.shimmerFrameLayout.startShimmer()
            holder.feedContent.visibility = View.GONE
        } else {


            holder.shimmerFrameLayout.stopShimmer()
            holder.shimmerFrameLayout.visibility = View.GONE
            holder.feedContent.visibility = View.VISIBLE


            val post = postList[position]
            holder.userName.text = post.username
            holder.description.text = post.description

            val displayName = if (post.userid == currentUserId && currentUserData != null) {
                currentUserData.username
            } else {
                post.username
            }
            holder.userName.text = displayName
            holder.description.text = post.description

            val currentUserId = Firebase.auth.currentUser?.uid
            if (post.likedBy.contains(currentUserId)) {
                holder.likeButton.setImageResource(R.drawable.photoquest_heart_icon)
            } else {
                holder.likeButton.setImageResource(R.drawable.photoquest_heart_icon_outline)
            }

            // Sätt bakgrund baserat på post.isChecked
            if (post.isChecked) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#70FFFF33"))

            } else {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#90FFFFFF"))
            }

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
                val currentUserid = Firebase.auth.currentUser?.uid
                //  fun for checking and saving like state and animation
                if (currentUserid != null && !post.likedBy.contains(currentUserid)) {
                    postVm.addLikesToPost(post.postId)
                    holder.likeButton.setImageResource(R.drawable.photoquest_heart_icon)

                    Toast.makeText(
                        holder.itemView.context,
                        holder.itemView.context.getString(R.string.successfully_added_like),
                        Toast.LENGTH_SHORT
                    ).show()

                    //  Heart animation control
                    holder.heartAnim.visibility = View.VISIBLE
                    holder.heartAnim.playAnimation()
                    holder.heartAnim.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {}
                        override fun onAnimationEnd(animation: Animator) {
                            holder.heartAnim.visibility = View.GONE
                            holder.heartAnim.removeAllAnimatorListeners()
                        }

                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    })
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        holder.itemView.context.getString(R.string.already_liked),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            holder.optionImage.setOnClickListener() {
                postVm.setItemId(post.postId)

                val moreOptionsFragment = MoreOptionsPostBottomSheetFragment()
                val activity = holder.itemView.context as? AppCompatActivity
                activity?.supportFragmentManager?.let {
                    moreOptionsFragment.show(it, moreOptionsFragment.tag)
                }
            }

            val profileUrl = if (post.userid == currentUserId) {
                currentUserProfileUrl
            } else {
                post.profilePic
            }
            Glide.with(holder.itemView.context)
                .load(profileUrl)
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
}