package com.raveendran.helpdevs.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.raveendran.helpdevs.other.Constants.KEY_NAME
import com.raveendran.helpdevs.other.SharedPrefs
import kotlinx.android.synthetic.main.start_fragment.*

class StartFragment : Fragment(R.layout.start_fragment) {

    private lateinit var sharedPref: SharedPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        val isFirstAppOpen = sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)

        if (!isFirstAppOpen) {
            print(sharedPref.getString(KEY_NAME, ""))
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.startFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_startFragment_to_todoFragment,
                savedInstanceState,
                navOptions
            )
        }

        continueBtn.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                findNavController().navigate(R.id.action_startFragment_to_todoFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = nameEt.text.toString()
        if (name.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        print(sharedPref.getString(KEY_NAME, ""))
        return true
    }


}