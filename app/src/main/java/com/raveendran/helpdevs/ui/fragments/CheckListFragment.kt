package com.raveendran.helpdevs.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.adapters.CheckListAdapter
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.viewmodels.TodoViewModel
import kotlinx.android.synthetic.main.check_list_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CheckListFragment : Fragment(R.layout.check_list_fragment) {

    private val viewModel: TodoViewModel by viewModels()
    private lateinit var checkListAdapter: CheckListAdapter
    private lateinit var sharedPref: SharedPreferences
    private val args: CheckListFragmentArgs by navArgs()
    private var userName = ""
    var progress = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()
        val todoData = args.checkList
        val highPriorityText = "🟢 High Priority"
        val lowPriorityText = "🔴 Low Priority"
        todoTv.text = todoData.todo
        timeTv.text = todoData.time
        noteTv.text = todoData.notes
        priorityTv.text =
            if (todoData.priority.equals("High", true)) highPriorityText else lowPriorityText
        observeList()
        setupRecyclerView()
        viewModel.fetchCheckList(userName, todoData.id)
        observeCount(todoData.id)
        addNewCheckList.setOnClickListener {
            val dialog = AddCheckListDialog(todoData)
            dialog.show(parentFragmentManager, "addCheckList")
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
//            if (it.isEmpty()) {
//                noDataView.visibility = View.VISIBLE
//            } else {
//                noDataView.visibility = View.GONE
//            }
        })
    }

    private fun observeCount(id: String) {
        viewModel.totalCheckCount.observe(viewLifecycleOwner, { count ->
            viewModel.totalCheckListSize.observe(viewLifecycleOwner, { size ->
                progressCount.text = "$count / $size Completed"
                val result = count / size.toDouble()
                totalProgress.progress = (result * 100).roundToInt()
                GlobalScope.launch {
                    viewModel.updatePercentage(userName, id, (result * 100).roundToInt())
                }
            })
        })
    }


    private fun setupRecyclerView() = checkListRecycler.apply {
        checkListAdapter = CheckListAdapter()
        adapter = checkListAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

}