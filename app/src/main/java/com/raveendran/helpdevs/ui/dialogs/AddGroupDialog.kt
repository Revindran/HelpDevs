package com.raveendran.helpdevs.ui.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.ChatGroup
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.viewmodels.ChatViewModel
import kotlinx.android.synthetic.main.add_group_dialog.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddGroupDialog : DialogFragment(R.layout.add_group_dialog) {

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private var userName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()
        addGroupBtn.setOnClickListener {
            createNewChatGroup(view)
        }
    }

    private fun createNewChatGroup(view: View) {
        val date = Date()
        val ft = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US)
        val currentDateTime = ft.format(date)
        val name = groupTitleET.text.toString()
        val data = ChatGroup(name, userName, currentDateTime, System.currentTimeMillis())
        if (name.isNotEmpty()) {
            GlobalScope.launch {
                viewModel.createNewChatGroup(data)
            }
            groupTitleET.setText("")
            dialog?.dismiss()
        } else Snackbar.make(
            view,
            "Please provide any name to create a group",
            Snackbar.LENGTH_SHORT
        ).show()
    }

}