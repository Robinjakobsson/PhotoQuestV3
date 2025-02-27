package com.example.photoquestv3.ViewModel

import androidx.lifecycle.ViewModel
import com.example.photoquestv3.Repositories.CommentRepository

class CommentViewModel: ViewModel()  {

    private val commentRepository = CommentRepository()
    val comments = commentRepository.comments

    /**
     * Listeners to comments
     */
    fun startListeningToComments(postId: String) {
        commentRepository.restartListeningToComments(postId)
    }

    /**
     * Adds a comment to the database
     */
    fun addComment(postId: String, commentText: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        commentRepository.addComment(postId, commentText, onSuccess, onFailure)
    }

    /**
     * Updates a comment in the database
     */
    fun updateComment(commentId: String, newText: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        commentRepository.updateComment(commentId, newText, onSuccess, onFailure)
    }

    /**
     * Deletes a comment from the database
     */
    fun deleteComment(commentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        commentRepository.deleteComment(commentId, onSuccess, onFailure)
    }

    /**
     * Stops listening to comments
     */
    override fun onCleared() {
        super.onCleared()
        commentRepository.stopListeningToComments()
    }

}