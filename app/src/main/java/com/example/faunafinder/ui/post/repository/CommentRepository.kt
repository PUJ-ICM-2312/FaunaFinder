package com.example.faunafinder.ui.post.repository

import com.example.faunafinder.ui.post.model.Comment
import com.google.firebase.database.*

object CommentRepository {
    private val db = FirebaseDatabase.getInstance().reference.child("comments")

    fun addComment(comment: Comment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newCommentRef = db.push()
        val commentWithId = comment.copy(id = newCommentRef.key ?: "")
        newCommentRef.setValue(commentWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Escuchar comentarios para un post en tiempo real
    fun listenCommentsForPost(postId: String, onChange: (List<Comment>) -> Unit, onError: (DatabaseError) -> Unit) {
        val query = db.orderByChild("postId").equalTo(postId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val comments = mutableListOf<Comment>()
                for (child in snapshot.children) {
                    val comment = child.getValue(Comment::class.java)
                    if (comment != null) comments.add(comment)
                }
                comments.sortBy { it.timestamp }
                onChange(comments)
            }
            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        }
        query.addValueEventListener(listener)
    }
}
