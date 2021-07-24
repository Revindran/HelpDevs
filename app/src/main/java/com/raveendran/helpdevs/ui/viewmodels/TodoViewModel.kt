package com.raveendran.helpdevs.ui.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.raveendran.helpdevs.models.Todo
import kotlinx.coroutines.tasks.await

class TodoViewModel : ViewModel() {

    val todos = MutableLiveData<List<Todo>>()

    fun fetchTodos(userName: String) {
        val db = FirebaseFirestore.getInstance().collection(userName)
        db.orderBy("timeStamp", Query.Direction.ASCENDING).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.size() > 0) {
                val items = mutableListOf<Todo>()
                for (docs in snapshot) {
                    val notes = docs.toObject(Todo::class.java)
                    notes.let {
                        items.add(it)
                    }
                    todos.value = items
                }
            } else {
                todos.value = emptyList()
            }
        }
    }

    suspend fun addNewTodo(todo: Todo, userName: String) {
        var id: String
        val db = FirebaseFirestore.getInstance().collection(userName)
        try {
            db.add(todo).await().get().addOnSuccessListener {
                id = it.id
                val data = hashMapOf("id" to id)
                db.document(id).update(data as Map<String, Any>)
            }

        } catch (e: Exception) {
            print(e.message)
        }
    }

    suspend fun deleteTodo(id: String, userName: String) {
        val db = FirebaseFirestore.getInstance().collection(userName).document(id)
        db.delete().await()
    }

    suspend fun saveTodo(todo: Todo, userName: String) {
        val db = FirebaseFirestore.getInstance().collection(userName).document(todo.id)
        db.set(todo).await()
    }

    suspend fun updateTodo(todo: Todo, userName: String) {
        val db = FirebaseFirestore.getInstance().collection(userName).document(todo.id)
        db.set(todo).await()
    }


}

