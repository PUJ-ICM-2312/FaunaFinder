package com.example.faunafinder.ui.post.repository

import com.example.faunafinder.ui.post.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

object PostsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")

    // Agregar nuevo post
    fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = postsCollection.document()
        val postWithId = post.copy(id = newDoc.id)
        newDoc.set(postWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Obtener lista de posts ordenada por fecha descendente
    fun getPosts(onSuccess: (List<Post>) -> Unit, onFailure: (Exception) -> Unit) {
        postsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.toObjects(Post::class.java)
                onSuccess(posts)
            }
            .addOnFailureListener { onFailure(it) }
    }

    // Incrementar contador de likes atomícamente
    fun incrementLikesCount(postId: String, increment: Int = 1) {
        val postRef = postsCollection.document(postId)
        val update = hashMapOf<String, Any>(
            "likesCount" to com.google.firebase.firestore.FieldValue.increment(increment.toLong())
        )
        postRef.set(update, SetOptions.merge())
    }

    // Incrementar contador de comentarios atomícamente
    fun incrementCommentsCount(postId: String, increment: Int = 1) {
        val postRef = postsCollection.document(postId)
        val update = hashMapOf<String, Any>(
            "commentsCount" to com.google.firebase.firestore.FieldValue.increment(increment.toLong())
        )
        postRef.set(update, SetOptions.merge())
    }
}
