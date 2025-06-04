package com.example.faunafinder.ui.notification

import com.example.faunafinder.ui.notification.Notification
import com.google.firebase.database.*

object NotificationRepository {
    private val db = FirebaseDatabase.getInstance().reference.child("notifications")

    fun addNotification(notification: Notification) {
        val ref = db.push()
        val notificationWithId = notification.copy(id = ref.key ?: "")
        ref.setValue(notificationWithId)
    }

    fun getNotificationsForUser(
        userId: String,
        onSuccess: (List<Notification>) -> Unit,
        onFailure: (DatabaseError) -> Unit
    ) {
        db.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = snapshot.children.mapNotNull {
                        it.getValue(Notification::class.java)
                    }.sortedByDescending { it.timestamp }

                    onSuccess(notifications)
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error)
                }
            })
    }
}
