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
                val data2 = hashMapOf(
                    "lastChat" to chat.text,
                    "lastChatMemberName" to chat.name,
                    "lastChatTime" to chat.time
                )
                FirebaseFirestore.getInstance().collection("ChatGroups").document(groupName)
                    .update(data2 as Map<String, Any>).addOnSuccessListener {
                        // TODO: 02-08-2021 Update the message count
                    }
            }
        } catch (e: Exception) {
            print(e.message)
        }
    }

    suspend fun isMemberAdded(groupName: String, uid: String): Boolean {
        val db = FirebaseFirestore.getInstance().collection("ChatGroups").document(groupName)
            .collection("members").document(uid).get().await()
        if (db.exists()) {
            return true
        }
        return false
    }

    suspend fun addMemberToGroup(groupName: String, uid: String) {
        val db = FirebaseFirestore.getInstance().collection("ChatGroups").document(groupName)
            .collection("members").document(uid)
        val data = hashMapOf("count" to 0, "uid" to uid)
        db.set(data as Map<String, Any>).await()
    }

    fun updateTheMessageCount(groupName: String) {
        val db = FirebaseFirestore.getInstance().collection("ChatGroups").document(groupName)
            .collection("members")
        db.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.size() > 0) {
                for (doc in snapshot) {
                    val count = doc["count"]
                    val id = doc.id
                }
            }
        }
    }

    private fun updateCount(groupName: String, uid: String, count: String) {
        var c = count.toInt()
        val data = hashMapOf("count" to c++)
        FirebaseFirestore.getInstance().collection("ChatGroups").document(groupName)
            .collection("members").document(uid).update(data as Map<String, Any>)
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

    private fun fetchChatGroups() {
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