package com.raveendran.helpdevs.ui.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.raveendran.helpdevs.models.Todo
import com.raveendran.helpdevs.models.TodoCheckList
import kotlinx.coroutines.tasks.await

class TodoViewModel : ViewModel() {

    val todos = MutableLiveData<List<Todo>>()
    val checkListLiveData = MutableLiveData<List<TodoCheckList>>()
    val totalCheckListSize = MutableLiveData<Int>()
    val totalCheckCount = MutableLiveData<Int>()

    fun fetchTodos(userName: String) {
        val db = FirebaseFirestore.getInstance().collection("Users").document(userName)
            .collection("Todo")
        db.orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener { snapshot, e ->
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
        val db = FirebaseFirestore.getInstance().collection("Users").document(userName)
            .collection("Todo")
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
        FirebaseFirestore.getInstance().collection("Users").document(userName)
            .collection("Todo").document(id).delete().await()
    }

    suspend fun saveTodo(todo: Todo, userName: String) {
        val db = FirebaseFirestore.getInstance().collection("Users").document(userName)
            .collection("Todo").document(todo.id)
        db.set(todo).await()
    }

    suspend fun updateTodo(todo: Todo, userName: String) {
        val data =
            hashMapOf(
                "notes" to todo.notes,
                "todo" to todo.todo,
                "priority" to todo.priority,
                "timeStamp" to System.currentTimeMillis()
            )
        val db = FirebaseFirestore.getInstance().collection("Users").document(userName)
            .collection("Todo").document(todo.id)
        db.update(data as Map<String, Any>).await()
    }

    suspend fun addCheckList(name: String, id: String, data: TodoCheckList) {
        val db =
            FirebaseFirestore.getInstance().collection("Users").document(name)
                .collection("Todo").document(id).collection("Checklist")
        db.add(data).await().get().addOnSuccessListener {
            try {
                db.document(id).update(hashMapOf("id" to it.id) as Map<String, Any>)
            } catch (e: Exception) {
                print(e.message)
            }
        }
    }

    fun fetchCheckList(name: String, id: String) {
        val db =
            FirebaseFirestore.getInstance().collection("Users").document(name)
                .collection("Todo").document(id).collection("Checklist")
        db.orderBy("timeStamp", Query.Direction.ASCENDING).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.size() > 0) {
                totalCheckListSize.postValue(snapshot.size())
                val items = mutableListOf<TodoCheckList>()
                var checkedItem = 0
                for (docs in snapshot) {
                    val checkList = docs.toObject(TodoCheckList::class.java)
                    if (checkList.checked) checkedItem++
                    checkList.let {
                        items.add(it)
                    }
                    totalCheckCount.postValue(checkedItem)
                    checkListLiveData.value = items
                }
            } else {
                totalCheckCount.postValue(0)
                checkListLiveData.value = emptyList()
            }
        }
    }

    suspend fun changeCheckStatus(name: String, docId: String, checkId: String, status: Boolean) {
        val data = hashMapOf("checked" to status)
        val db =
            FirebaseFirestore.getInstance().collection("Users").document(name)
                .collection("Todo").document(docId).collection("Checklist")
                .document(checkId)
        db.update(data as Map<String, Any>).await()
    }

    suspend fun updatePercentage(name: String, docId: String, status: Int) {
        val data = hashMapOf("progress" to status)
        val db = FirebaseFirestore.getInstance().collection("Users").document(name)
            .collection("Todo").document(docId)
        db.update(data as Map<String, Any>).await()
    }

}

