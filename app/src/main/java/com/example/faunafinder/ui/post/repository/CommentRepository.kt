package com.example.faunafinder.ui.post.repository

import com.example.faunafinder.ui.post.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object CommentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val commentsCollection = db.collection("comments")

    fun addComment(comment: Comment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = commentsCollection.document()
        val commentWithId = comment.copy(id = newDoc.id)
        newDoc.set(commentWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getCommentsForPost(postId: String, onSuccess: (List<Comment>) -> Unit, onFailure: (Exception) -> Unit) {
        commentsCollection.whereEqualTo("postId", postId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val comments = snapshot.toObjects(Comment::class.java)
                onSuccess(comments)
            }
            .addOnFailureListener { onFailure(it) }
    }
}
