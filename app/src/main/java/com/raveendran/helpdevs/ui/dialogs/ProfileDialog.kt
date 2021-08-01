package com.raveendran.helpdevs.ui.dialogs

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.other.SharedPrefs
import com.raveendran.helpdevs.ui.activity.LoginActivity
import kotlinx.android.synthetic.main.profile_dialog.*

class ProfileDialog(
    private val userName: String,
    private val userImage: String,
    private val userUid: String,
    private val userMail: String
) :
    DialogFragment(R.layout.profile_dialog) {
    override fun getTheme() = R.style.AlertDialogTheme

    private lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        auth = Firebase.auth

        nameTv.text = userName
        emailTv.text = userMail
        uidTv.text = userUid
        Glide.with(this)
            .load(userImage).into(profileImageView)

        logoutBtn.setOnClickListener {
            auth.signOut().also {
                Toast.makeText(context, "Logout Successful", Toast.LENGTH_SHORT).show()
            }
            updateUI()
        }
    }

    private fun updateUI() {
        if (auth.currentUser == null) {
            navigateToLogin()
        } else {
            Toast.makeText(context, "Something went wrong! Try again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLogin() {
        SharedPrefs.clearSharedPrefs(sharedPref)
        startActivity(Intent(context, LoginActivity::class.java))
        activity?.finish()
    }

}