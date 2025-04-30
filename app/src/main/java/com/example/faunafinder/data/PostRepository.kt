package com.example.faunafinder.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class PostRepository {

    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")

    // Obtener todos los posts ordenados por fecha (más recientes primero)
    suspend fun getAllPosts(): List<Post> {
        return try {
            val snapshot = postsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Agregar un nuevo post a Firestore
    suspend fun addPost(post: Post) {
        try {
            postsCollection.add(post).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Agregar o quitar like de un usuario (toggle)
    suspend fun toggleLike(postId: String, userId: String) {
        try {
            val docRef = postsCollection.document(postId)
            val snapshot = docRef.get().await()
            val post = snapshot.toObject(Post::class.java) ?: return

            val updatedLikes = if (userId in post.likes) {
                post.likes - userId
            } else {
                post.likes + userId
            }

            docRef.update("likes", updatedLikes).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Agregar un comentario a un post
    suspend fun addComment(postId: String, comment: Comment) {
        try {
            val docRef = postsCollection.document(postId)
            val snapshot = docRef.get().await()
            val post = snapshot.toObject(Post::class.java) ?: return

            val updatedComments = post.comments + comment
            docRef.update("comments", updatedComments).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
