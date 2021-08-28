package com.raveendran.helpdevs.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.adapters.CheckListAdapter
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.dialogs.AddCheckListDialog
import com.raveendran.helpdevs.ui.dialogs.UpdateTodoDialog
import com.raveendran.helpdevs.ui.viewmodels.TodoViewModel
import kotlinx.android.synthetic.main.check_list_fragment.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CheckListFragment : Fragment(R.layout.check_list_fragment) {

    private val viewModel: TodoViewModel by viewModels()
    private lateinit var checkListAdapter: CheckListAdapter
    private lateinit var sharedPref: SharedPreferences
    private val args: CheckListFragmentArgs by navArgs()
    private var userName = ""

    @DelicateCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()
        val todoData = args.checkList
        val highPriorityText = "\uD83D\uDFE2 High Priority"
        val lowPriorityText = "\uD83D\uDFE0 Low Priority"
        todoTv.text = todoData.todo
        timeTv.text = todoData.time
        noteTv.text = todoData.notes
        priorityTv.text =
            if (todoData.priority.equals("High", true)) highPriorityText else lowPriorityText
        observeList()
        setupRecyclerView()
        viewModel.fetchCheckList(userName, todoData.id)
        observeCount(todoData.id)
        val noDataText =
            "Dear $userName. Your checklist is empty.\nYou can Create one using the below Floating button!"
        noDataTv.text = noDataText
        addNewCheckList.setOnClickListener {
            val dialog = AddCheckListDialog(todoData)
            dialog.show(parentFragmentManager, "addCheckList")
        }
        editButton.setOnClickListener {
            val updateTodoDialog = UpdateTodoDialog(todoData)
            updateTodoDialog.show(parentFragmentManager, "msg")
        }
        checkListAdapter.setOnItemClickListener {
            if (it.checked) {
                GlobalScope.launch {
                    viewModel.changeCheckStatus(userName, todoData.id, it.id, false)
                }
            } else {
                GlobalScope.launch {
                    viewModel.changeCheckStatus(userName, todoData.id, it.id, true)
                }
            }
        }
    }

    private fun observeList() {
        viewModel.checkListLiveData.observe(viewLifecycleOwner, {
            checkListAdapter.submitList(it)
            if (it.isEmpty()) {
                noDataView.visibility = View.VISIBLE
            } else {
                noDataView.visibility = View.GONE
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun observeCount(id: String) {
        viewModel.totalCheckCount.observe(viewLifecycleOwner, { count ->
            viewModel.totalCheckListSize.observe(viewLifecycleOwner, { size ->
                progressCount.text = "$count / $size Completed"
                val result = count / size.toDouble()
                totalProgress.progress = (result * 100).roundToInt()
                lifecycleScope.launch {
                    viewModel.updatePercentage(userName, id, (result * 100).roundToInt())
                }
            })
        })
    }


    private fun setupRecyclerView() = checkListRecycler.apply {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !addNewCheckList.isShown) addNewCheckList.show() else if (dy > 0 && addNewCheckList.isShown) addNewCheckList.hide()
            }
        })
        checkListAdapter = CheckListAdapter(context)
        adapter = checkListAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}