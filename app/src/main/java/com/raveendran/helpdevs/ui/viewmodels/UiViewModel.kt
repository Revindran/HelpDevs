package com.raveendran.helpdevs.ui.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.raveendran.helpdevs.models.Dev

class UiViewModel : ViewModel() {

    val devItems = MutableLiveData<List<Dev>>()

    init {
        fetchDevData()
    }

    private fun fetchDevData() {
        val db = FirebaseFirestore.getInstance().collection("Dev")
        db.orderBy("id", Query.Direction.ASCENDING).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.size() > 0) {
                val items = mutableListOf<Dev>()
                for (docs in snapshot) {
                    val notes = docs.toObject(Dev::class.java)
                    notes.let {
                        items.add(it)
                    }
                    devItems.value = items
                }
            } else {
                devItems.value = emptyList()
            }
        }
    }

}