package com.example.faunafinder.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val repository = PostRepository()

    // Estado de la lista de publicaciones
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado de error opcional
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadPosts()
    }

    // Cargar publicaciones desde Firestore
    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getAllPosts()
                _posts.value = result
            } catch (e: Exception) {
                _error.value = "Error al cargar publicaciones"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Crear nueva publicación
    fun createPost(post: Post) {
        viewModelScope.launch {
            repository.addPost(post)
            loadPosts() // recargar para incluir la nueva
        }
    }

    // Dar o quitar like
    fun toggleLike(postId: String, userId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId, userId)
            loadPosts()
        }
    }

    // Añadir comentario
    fun addComment(postId: String, comment: Comment) {
        viewModelScope.launch {
            repository.addComment(postId, comment)
            loadPosts()
        }
    }
}
