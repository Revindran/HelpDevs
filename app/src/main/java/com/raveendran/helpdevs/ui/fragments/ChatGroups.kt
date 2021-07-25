package com.raveendran.helpdevs.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.adapters.ChatGroupsAdapter
import com.raveendran.helpdevs.ui.viewmodels.ChatViewModel
import kotlinx.android.synthetic.main.chat_group_fragment.*

class ChatGroups : Fragment(R.layout.chat_group_fragment) {

    private val dialog = AddGroupDialog()
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatGroupsAdapter: ChatGroupsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeList()
        addGroupButton.setOnClickListener {
            dialog.show(parentFragmentManager, "addChat")
        }

        chatGroupsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("groupData", it)
            }
            findNavController().navigate(R.id.action_chatGroups_to_chatFragment, bundle)
        }

    }

    private fun observeList() {
        viewModel.chatGroups.observe(viewLifecycleOwner, {
            chatGroupsAdapter.submitList(it)
            if (it.isEmpty()) {
                noGroupsView.visibility = View.VISIBLE
            } else {
                noGroupsView.visibility = View.GONE
            }
        })
    }

    private fun setupRecyclerView() = groupsRv.apply {
        chatGroupsAdapter = ChatGroupsAdapter()
        adapter = chatGroupsAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}