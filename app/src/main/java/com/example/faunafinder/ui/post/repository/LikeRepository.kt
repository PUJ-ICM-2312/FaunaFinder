package com.example.faunafinder.ui.post.repository

import com.example.faunafinder.ui.post.model.Like
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object LikeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val likesCollection = db.collection("likes")

    fun addLike(like: Like, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = likesCollection.document()
        val likeWithId = like.copy(id = newDoc.id)
        newDoc.set(likeWithId)
            .addOnSuccessListener {
                updateLikesCount(like.postId, 1)
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun removeLike(likeId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        likesCollection.document(likeId).get()
            .addOnSuccessListener { snapshot ->
                val like = snapshot.toObject(Like::class.java)
                if (like != null) {
                    likesCollection.document(likeId).delete()
                        .addOnSuccessListener {
                            updateLikesCount(like.postId, -1)
                            onSuccess()
                        }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(Exception("Like no encontrado"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }


    fun getUserLikeForPost(postId: String, userId: String, onSuccess: (Like?) -> Unit, onFailure: (Exception) -> Unit) {
        likesCollection.whereEqualTo("postId", postId).whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val like = if (!snapshot.isEmpty) snapshot.documents[0].toObject(Like::class.java) else null
                onSuccess(like)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getLikesCountForPost(postId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        likesCollection.whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot.size())
            }
            .addOnFailureListener { onFailure(it) }
    }

    private fun updateLikesCount(postId: String, increment: Long) {
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postId)
        postRef.update("likesCount", FieldValue.increment(increment))
    }


    }
