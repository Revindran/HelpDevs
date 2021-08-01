package com.raveendran.helpdevs.ui.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.Todo
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.viewmodels.TodoViewModel
import kotlinx.android.synthetic.main.todo_add_dialog.radioGroup
import kotlinx.android.synthetic.main.todo_update_dialog.*
import kotlinx.android.synthetic.main.todo_update_dialog.view.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UpdateTodoDialog(todoItem: Todo) : DialogFragment(R.layout.todo_update_dialog) {
    override fun getTheme() = R.style.AlertDialogTheme
    private val todo = todoItem
    private val viewModel: TodoViewModel by viewModels()
    private var radioData = ""
    private lateinit var sharedPref: SharedPreferences
    private var userName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()
        onRadioButtonClicked()
        updateTodoBtn.setOnClickListener {
            updateTodo(view)
            dialog?.dismiss()
        }
        updateTitleET.setText(todo.todo)
        updateNotesET.setText(todo.notes)
        if (todo.priority.equals("high", true)) updateRadioHighBtn.isChecked =
            true else updateRadioLowBtn.isChecked = true
    }

    private fun updateTodo(view: View) {
        val date = Date()
        val ft = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US)
        val currentDateTime = ft.format(date)
        if (validate(view)) {
            val data = Todo(
                updateTitleET.text.toString(),
                currentDateTime,
                System.currentTimeMillis(),
                radioData,
                updateNotesET.text.toString(),
                todo.id
            )
            lifecycleScope.launch {
                viewModel.updateTodo(data, userName)
            }

            Toast.makeText(context, "Todo Updated Successfully", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Fill All the fields to continue", Toast.LENGTH_LONG).show()
        }
    }

    private fun onRadioButtonClicked() {
        radioGroup.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.updateRadioHighBtn -> radioData = "High"
                R.id.updateRadioLowBtn -> radioData = "Low"
            }
        }
    }

    private fun validate(view: View): Boolean {
        return view.updateNotesET.text.toString().isNotEmpty() && view.updateTitleET.text.toString()
            .isNotEmpty() && radioData.isNotEmpty()
    }
}