package com.example.faunafinder.ui.post.repository

import com.example.faunafinder.ui.post.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue


object PostsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")

    fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = postsCollection.document()
        val postWithId = post.copy(id = newDoc.id)
        newDoc.set(postWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun listenPosts(onChange: (List<Post>) -> Unit, onError: (Exception) -> Unit) {
        postsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }
            val posts = snapshot?.documents?.mapNotNull { it.toObject(Post::class.java) } ?: emptyList()
            onChange(posts.sortedByDescending { it.timestamp })
        }
    }

    fun incrementCommentsCount(postId: String) {
        val db = FirebaseFirestore.getInstance()
        val postRef = db.collection("posts").document(postId)
        postRef.update("commentsCount", FieldValue.increment(1))
    }

    fun getPostById(postId: String, onSuccess: (Post?) -> Unit, onFailure: (Exception) -> Unit) {
        postsCollection.document(postId).get()
            .addOnSuccessListener { snapshot ->
                val post = snapshot.toObject(Post::class.java)
                onSuccess(post)
            }
            .addOnFailureListener { onFailure(it) }
    }


}