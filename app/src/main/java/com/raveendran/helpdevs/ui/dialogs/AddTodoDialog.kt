package com.raveendran.helpdevs.ui.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.Todo
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.viewmodels.TodoViewModel
import kotlinx.android.synthetic.main.todo_add_dialog.*
import kotlinx.android.synthetic.main.todo_add_dialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTodoDialog : DialogFragment(R.layout.todo_add_dialog) {
    override fun getTheme() = R.style.AlertDialogTheme
    private val viewModel: TodoViewModel by viewModels()
    private var radioData = ""
    private lateinit var sharedPref: SharedPreferences

    private var userName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!

        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()

        onRadioButtonClicked()
        addGroupBtn.setOnClickListener {
            addTodo(view)
        }
    }

    private fun addTodo(view: View) {
        val date = Date()
        val ft = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US)
        val currentDateTime = ft.format(date)
        if (validate(view)) {
            val data = Todo(
                titleET.text.toString(),
                currentDateTime,
                System.currentTimeMillis(),
                radioData,
                notesET.text.toString()
            )
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.addNewTodo(data, userName)
            }

            Toast.makeText(context, "Note Added Successfully", Toast.LENGTH_LONG).show()
            clearDialogData()
        } else {
            Toast.makeText(context, "Fill All the fields to continue", Toast.LENGTH_LONG).show()
        }
    }


    private fun clearDialogData() {
        titleET.setText("")
        notesET.setText("")
        radioHighBtn.isChecked = false
        radioLowBtn.isChecked = false
        dialog?.dismiss()
    }

    private fun onRadioButtonClicked() {
        radioGroup.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.radioHighBtn -> radioData = "High"
                R.id.radioLowBtn -> radioData = "Low"
            }
        }
    }

    private fun validate(view: View): Boolean {
        return view.notesET.text.toString().isNotEmpty() && view.titleET.text.toString()
            .isNotEmpty() && radioData.isNotEmpty()
    }


}