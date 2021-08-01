package com.raveendran.helpdevs.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.adapters.DevAdapter
import com.raveendran.helpdevs.ui.viewmodels.UiViewModel
import kotlinx.android.synthetic.main.dev_fragment.*

class DevFragment : Fragment(R.layout.dev_fragment) {

    private val viewModel: UiViewModel by viewModels()
    private lateinit var devAdapter: DevAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.devItems.observe(viewLifecycleOwner, {
            devAdapter.submitList(it)
        })

        devAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("devData", it)
            }
            findNavController().navigate(
                R.id.action_uiViewsFragment_to_devWebViewFragment,
                bundle
            )
        }
    }

    private fun setupRecyclerView() = devRecycler.apply {
        devAdapter = DevAdapter(context)
        adapter = devAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}