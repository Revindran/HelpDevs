package com.raveendran.helpdevs.ui.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.raveendran.helpdevs.models.Chat
import com.raveendran.helpdevs.models.ChatGroup
import kotlinx.coroutines.tasks.await

class ChatViewModel : ViewModel() {

    val chats = MutableLiveData<List<Chat>>()
    val chatGroups = MutableLiveData<List<ChatGroup>>()

    init {
        fetchChatGroups()
    }

    suspend fun addChat(groupName: String, chat: Chat) {
        var id: String
        val db = FirebaseFirestore.getInstance().collection("ChatGroups").document(groupName)
            .collection("chat")
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

    fun fetchChats(groupName: String) {
        val db =
            FirebaseFirestore.getInstance().collection("ChatGroups").document(groupName)
                .collection("chat")
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

    fun fetchChatGroups() {
        val db = FirebaseFirestore.getInstance().collection("ChatGroups")
        db.orderBy("timeStamp", Query.Direction.ASCENDING).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.size() > 0) {
                val items = mutableListOf<ChatGroup>()
                for (docs in snapshot) {
                    val groups = docs.toObject(ChatGroup::class.java)
                    groups.let {
                        items.add(it)
                    }
                    chatGroups.value = items
                }
            } else {
                chatGroups.value = emptyList()
            }
        }
    }


    suspend fun createNewChatGroup(data: ChatGroup) {
        val db = FirebaseFirestore.getInstance().collection("ChatGroups")
        db.document(data.groupName).set(data).await()
    }

}