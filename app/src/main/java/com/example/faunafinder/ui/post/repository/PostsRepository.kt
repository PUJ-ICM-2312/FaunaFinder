package com.example.faunafinder.ui.post.repository

import com.example.faunafinder.ui.post.model.Post
import com.google.firebase.database.*

object PostsRepository {
    private val db = FirebaseDatabase.getInstance().reference.child("posts")

    fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newPostRef = db.push()
        val postWithId = post.copy(id = newPostRef.key ?: "")
        newPostRef.setValue(postWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Escuchar posts en tiempo real
    fun listenPosts(onChange: (List<Post>) -> Unit, onError: (DatabaseError) -> Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                for (child in snapshot.children) {
                    val post = child.getValue(Post::class.java)
                    if (post != null) posts.add(post)
                }
                posts.sortByDescending { it.timestamp }
                onChange(posts)
            }
            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        }
        db.addValueEventListener(listener)
    }

    fun incrementLikesCount(postId: String, increment: Int = 1) {
        val postLikesRef = db.child(postId).child("likesCount")
        postLikesRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentValue = currentData.getValue(Int::class.java) ?: 0
                currentData.value = currentValue + increment
                return Transaction.success(currentData)
            }
            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {}
        })
    }

    fun incrementCommentsCount(postId: String, increment: Int = 1) {
        val postCommentsRef = db.child(postId).child("commentsCount")
        postCommentsRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentValue = currentData.getValue(Int::class.java) ?: 0
                currentData.value = currentValue + increment
                return Transaction.success(currentData)
            }
            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {}
        })
    }
}
