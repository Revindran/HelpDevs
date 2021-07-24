package com.raveendran.helpdevs.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.adapters.TodoAdapter
import com.raveendran.helpdevs.other.Constants.KEY_NAME
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.viewmodels.TodoViewModel
import kotlinx.android.synthetic.main.todo_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TodoFragment : Fragment(R.layout.todo_fragment) {

    private val viewModel: TodoViewModel by viewModels()
    private lateinit var todoAdapter: TodoAdapter

    private lateinit var sharedPref: SharedPreferences


    private val dialog = AddTodoDialog()
    private var userName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!

        userName = sharedPref.getString(KEY_NAME, "").toString()

        viewModel.fetchTodos(userName)


        val noDataText =
            "Hey $userName. It's seems like you currently don't have any work i guess. So why don't you Go And Watch Philips new Vids. cuz it's worth watching"
        noDataTv2.text = noDataText

        observeList()
        setupRecyclerView()

        todoAdapter.setOnItemClickListener {
            val updateTodoDialog = UpdateTodoDialog(it)
            updateTodoDialog.show(parentFragmentManager, "msg")
        }
        floatAddBtn.setOnClickListener {
            dialog.show(parentFragmentManager, "tag")
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val todo = todoAdapter.differ.currentList[position]
                GlobalScope.launch {
                    viewModel.deleteTodo(todo.id, userName)
                }
                Snackbar.make(view, "Successfully deleted note", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        GlobalScope.launch {
                            viewModel.saveTodo(todo, userName)
                        }
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(todoRecycler)
        }

    }

//    @SuppressLint("HardwareIds")
//    fun getDeviceId(): String {
//        return Settings.Secure.getString(
//            Application().contentResolver,
//            Settings.Secure.ANDROID_ID
//        )
//    }

    private fun observeList() {
        viewModel.todos.observe(viewLifecycleOwner, {
            todoAdapter.submitList(it)
            if (it.isEmpty()) {
                noDataView.visibility = View.VISIBLE
            } else {
                noDataView.visibility = View.GONE
            }
        })
    }


    private fun setupRecyclerView() = todoRecycler.apply {
        todoAdapter = TodoAdapter()
        adapter = todoAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}
