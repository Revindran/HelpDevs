package com.raveendran.helpdevs.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.adapters.TodoAdapter
import com.raveendran.helpdevs.other.Constants.KEY_EMAIL
import com.raveendran.helpdevs.other.Constants.KEY_NAME
import com.raveendran.helpdevs.other.Constants.KEY_UID
import com.raveendran.helpdevs.other.Constants.KEY_USER_IMAGE
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.dialogs.AddTodoDialog
import com.raveendran.helpdevs.ui.dialogs.ProfileDialog
import com.raveendran.helpdevs.ui.viewmodels.TodoViewModel
import kotlinx.android.synthetic.main.todo_fragment.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TodoFragment : Fragment(R.layout.todo_fragment) {

    private val viewModel: TodoViewModel by viewModels()
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private val dialog = AddTodoDialog()
    private var userName = ""
    private var userImage = ""
    private var userUid = ""
    private var userMail = ""

    @DelicateCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        userName = sharedPref.getString(KEY_NAME, "").toString()
        userUid = sharedPref.getString(KEY_UID, "").toString()
        userMail = sharedPref.getString(KEY_EMAIL, "").toString()
        userImage = sharedPref.getString(KEY_USER_IMAGE, "").toString()
        viewModel.fetchTodos(userName)
        val anim = AnimationUtils.loadAnimation(context, R.anim.simple_anim)
        greetingEt.startAnimation(anim)
        currentime.startAnimation(anim)
        currentime.text =
            if (getCurrentTime() <= 11) "\uD83C\uDF04" else if (getCurrentTime() <= 16) "\uD83C\uDF1E" else "\uD83C\uDF03"
        greetingEt.text =
            when {
                getCurrentTime() <= 11 -> "Good Morning $userName!!"
                getCurrentTime() <= 16 -> "Good Afternoon $userName!!"
                getCurrentTime() <= 20 -> "Good Evening $userName!!"
                else -> "Good Night Have A Great Sleep Mate!"
            }
        auth = Firebase.auth
        profileImageView.setOnClickListener {
            val dialog = ProfileDialog(userName, userImage, userUid, userMail)
            dialog.show(parentFragmentManager, "ProfileDialog")
        }
        Glide.with(this)
            .load(userImage).into(profileImageView)
        val noDataText =
            "Hii $userName. Currently you don't have any Todos.\nIf you want to create one.\nClick the below Floating Button"
        noDataTv2.text = noDataText
        observeList()
        setupRecyclerView()
        todoAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("checkList", it)
            }
            findNavController().navigate(R.id.action_todoFragment_to_checkListFragment, bundle)
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

    private fun getCurrentTime(): Int {
        return String.format("%1\$TH", System.currentTimeMillis()).toInt()
    }

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
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !floatAddBtn.isShown) floatAddBtn.show() else if (dy > 0 && floatAddBtn.isShown) floatAddBtn.hide()
            }
        })
        todoAdapter = TodoAdapter(context)
        adapter = todoAdapter
        layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
    }
}
