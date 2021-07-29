package com.raveendran.helpdevs.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.Todo
import com.raveendran.helpdevs.models.TodoCheckList
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.viewmodels.TodoViewModel
import kotlinx.android.synthetic.main.add_new_checklist.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddCheckListDialog(data: Todo) : DialogFragment(R.layout.add_new_checklist) {

    private val todo = data

    private val viewModel: TodoViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private var userName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()
        addChkListBtn.setOnClickListener {
            addNewCheckList(view)
        }
    }

    private fun addNewCheckList(view: View) {
        val date = Date()
        val ft = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US)
        val currentDateTime = ft.format(date)
        val title = chkListTitleET.text
        val data = TodoCheckList(
            title.toString(),
            false,
            "",
            0,
            System.currentTimeMillis(),
            currentDateTime
        )
        if (title.toString().isNotEmpty()) {
            GlobalScope.launch {
                viewModel.addCheckList(userName, todo.id, data)
            }
            chkListTitleET.setText("")
            dialog?.dismiss()
        } else Snackbar.make(
            view,
            "Please provide some title",
            Snackbar.LENGTH_SHORT
        ).show()
    }

}