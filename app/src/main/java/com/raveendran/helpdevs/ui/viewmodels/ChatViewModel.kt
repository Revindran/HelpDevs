package com.raveendran.helpdevs.ui.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.raveendran.helpdevs.models.Chat
import kotlinx.coroutines.tasks.await

class ChatViewModel : ViewModel() {

    val chats = MutableLiveData<List<Chat>>()

    init {
        fetchChats()
    }

    suspend fun addChat(chat: Chat) {
        var id: String
        val db = FirebaseFirestore.getInstance().collection("Message")
        try {
            db.add(chat).await().get().addOnSuccessListener {
                id = it.id
                val data = hashMapOf("id" to id)
                db.document(id).update(data as Map<String, Any>)
            }

        } catch (e: Exception) {
            print(e.message)
        }
    }

    private fun fetchChats() {
        val db = FirebaseFirestore.getInstance().collection("Message")
        db.orderBy("timeStamp", Query.Direction.ASCENDING).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.size() > 0) {
                val items = mutableListOf<Chat>()
                for (docs in snapshot) {
                    val notes = docs.toObject(Chat::class.java)
                    notes.let {
                        items.add(it)
                    }
                    chats.value = items
                }
            } else {
                chats.value = emptyList()
            }
        }
    }

}