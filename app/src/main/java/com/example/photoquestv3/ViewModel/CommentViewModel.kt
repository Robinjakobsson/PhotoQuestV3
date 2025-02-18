package com.example.photoquestv3.ViewModel

import androidx.lifecycle.ViewModel
import com.example.photoquestv3.Repositories.CommentRepository

class CommentViewModel: ViewModel()  {

    private val commentRepository = CommentRepository()

    fun addComment(postId: String, commentText: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        commentRepository.addComment(postId, commentText, onSuccess, onFailure)
    }

}