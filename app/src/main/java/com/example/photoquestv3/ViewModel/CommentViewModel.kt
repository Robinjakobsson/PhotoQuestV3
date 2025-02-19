package com.example.photoquestv3.ViewModel

import androidx.lifecycle.ViewModel
import com.example.photoquestv3.Repositories.CommentRepository

class CommentViewModel: ViewModel()  {

    private val commentRepository = CommentRepository()
    val comments = commentRepository.comments

    fun startListeningToComments(postId: String) {
        commentRepository.restartListeningToComments(postId)
    }

    fun addComment(postId: String, commentText: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        commentRepository.addComment(postId, commentText, onSuccess, onFailure)
    }

    fun updateComment(commentId: String, newText: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        commentRepository.updateComment(commentId, newText, onSuccess, onFailure)
    }

    override fun onCleared() {
        super.onCleared()
        commentRepository.stopListeningToComments()
    }

}