package com.raveendran.helpdevs.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.adapters.ChatAdapter
import com.raveendran.helpdevs.models.Chat
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.viewmodels.ChatViewModel
import kotlinx.android.synthetic.main.chat_fragment.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment(R.layout.chat_fragment) {

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var sharedPref: SharedPreferences
    private var userName = ""
    private var userID = ""
    private val args: ChatFragmentArgs by navArgs()

    @DelicateCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val groups = args.groupData
        setupRecyclerView()
        observeList()
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()
        userID = sharedPref.getString(Constants.KEY_UID, "").toString()

        grpNameTv.text = groups.groupName
        catChip.text = groups.groupCategory
        viewModel.fetchChats(groups.groupName)

        val noDataText =
            "Dear $userName. Your Chat box is empty.\nYou can Be the first one to start the chat!"
        noChatTv.text = noDataText
        floatingActionButton.setOnClickListener {
            it.hideKeyboard()
            addMessageToFB(groups.groupName)
        }
    }

    @DelicateCoroutinesApi
    private fun addMessageToFB(groupName: String) {
        GlobalScope.launch {
            val text = msgEt.text.toString()
            if (text.isNotEmpty()) {
                val date = Date()
                val ft = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US)
                val currentDateTime = ft.format(date)
                val chat = Chat(
                    text,
                    currentDateTime,
                    System.currentTimeMillis(),
                    "",
                    userName
                )
                viewModel.addChat(groupName, chat)
            }
        }
        msgEt.setText("")
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun observeList() {
        viewModel.chats.observe(viewLifecycleOwner, {
            chatAdapter.submitList(it)
            if (it.isEmpty()) noChatView.visibility = View.VISIBLE
            else noChatView.visibility = View.GONE
        })
    }

    private fun setupRecyclerView() = chatRecycler.apply {
        chatAdapter = ChatAdapter()
        adapter = chatAdapter
        layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
    }
}