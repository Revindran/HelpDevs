package com.raveendran.helpdevs.ui.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.ChatGroup
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.viewmodels.ChatViewModel
import kotlinx.android.synthetic.main.add_group_dialog.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddGroupDialog : DialogFragment(R.layout.add_group_dialog) {
    override fun getTheme() = R.style.AlertDialogTheme
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private var userName = ""

    @DelicateCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()
        addGroupBtn.setOnClickListener {
            createNewChatGroup(view)
        }
    }

    @DelicateCoroutinesApi
    private fun createNewChatGroup(view: View) {
        val chip = chipGroup?.children?.toList()?.filter { (it as Chip).isChecked }
            ?.joinToString(", ") { (it as Chip).text }
        val date = Date()
        val ft = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US)
        val currentDateTime = ft.format(date)
        val name = groupTitleET.text.toString()
        val data = ChatGroup(
            name,
            userName,
            currentDateTime,
            System.currentTimeMillis(),
            groupCategory = chip.toString()
        )
        if (name.isNotEmpty() && chip.toString().isNotEmpty()) {
            GlobalScope.launch {
                viewModel.createNewChatGroup(data)
            }
            groupTitleET.setText("")
            Toast.makeText(context, "Group Added Successful", Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        } else
            Toast.makeText(
                context,
                "Please give a name and select a category to create a group",
                Toast.LENGTH_SHORT
            )
                .show()
    }

}