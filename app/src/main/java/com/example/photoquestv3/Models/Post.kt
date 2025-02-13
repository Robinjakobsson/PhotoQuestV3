package com.example.photoquestv3.Models

import com.example.photoquestv3.R
import com.google.firebase.Timestamp

data class Post(
    val postId: String = "",
    val username: String = "",
    val profilePic: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val userId: String = "",
    val likes: Int = 0,
    val timestamp: Timestamp? = null // Timestamp implemented
) {
    companion object {

//        fun mockData(): List<Post> {
//            return listOf(
//                Post(
//                    postId = "1",
//                    username = "LukeSkywalker",
//                    profilePic = "R.drawable.ic_person",
//                    imagePostUrl = "https://images.unsplash.com/photo-1569793667639-dae11573b34f?q=80&w=4740&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
//                    description = "Ah yes, those are my first photos",
//                    userId = "1",
//                    likes = 0,
//                ),
//                Post(
//                    postId = "2",
//                    username = "DarthVader",
//                    profilePic = "R.drawable.ic_person",
//                    imagePostUrl = "https://plus.unsplash.com/premium_photo-1722018576685-45a415a4ff67?q=80&w=4632&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
//                    description = "We finally meet again!",
//                    userId = "2",
//                    likes = 3,
//                ),
//                Post(
//                    postId = "3",
//                    username = "ObiWanKenobi",
//                    profilePic = "R.drawable.ic_person",
//                    imagePostUrl = "https://images.unsplash.com/photo-1524582642571-404d774f344f?q=80&w=4000&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
//                    description = "Well hello there!",
//                    userId = "3",
//                    likes = 5,
//                ),
//                Post(
//                    postId = "4",
//                    username = "Yoda",
//                    profilePic = R.drawable.ic_person,
//                    imagePostUrl = "https://images.unsplash.com/photo-1599002762948-19068b069803?q=80&w=4057&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
//                    description = "Much to learn, you still have",
//                    userId = "4",
//                    likes = 2,
//                ),
//                Post(
//                    postId = "5",
//                    username = "C3pO",
//                    profilePic = R.drawable.ic_person,
//                    imagePostUrl = "https://images.unsplash.com/photo-1599272771314-f3ec16bda3f2?q=80&w=4740&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
//                    description = "First selfie",
//                    userId = "5",
//                    likes = 1,
//                ),
//                Post(
//                    postId = "6",
//                    username = "StormTrooper12",
//                    profilePic = R.drawable.ic_person,
//                    imagePostUrl = "https://images.unsplash.com/photo-1478479405421-ce83c92fb3ba?q=80&w=2787&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
//                    description = "Ah here we go again..",
//                    userId = "6",
//                    likes = 0,
//                )
//            )
//        }
    }
}