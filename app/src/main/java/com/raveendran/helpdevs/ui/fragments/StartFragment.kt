package com.raveendran.helpdevs.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.other.Constants.KEY_EMAIL
import com.raveendran.helpdevs.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.raveendran.helpdevs.other.Constants.KEY_NAME
import com.raveendran.helpdevs.other.Constants.KEY_UID
import com.raveendran.helpdevs.other.Constants.KEY_USER_IMAGE
import com.raveendran.helpdevs.other.SharedPrefs
import kotlinx.android.synthetic.main.start_fragment.*

class StartFragment : Fragment(R.layout.start_fragment) {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val gTAG = "Google Sign In"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = context?.let { SharedPrefs.sharedPreferences(it) }!!
        auth = Firebase.auth
        val currentUser = auth.currentUser
        updateUI(currentUser, savedInstanceState)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        googleSignButton.setOnClickListener {
            signIn()
        }
    }

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(gTAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(gTAG, "Google sign in failed", e)
                }
            }
        }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        launchSomeActivity.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                val firebaseUser = auth.currentUser

                val uid = firebaseUser?.uid
                val name = firebaseUser?.displayName
                val email = firebaseUser?.email
                val userImage = firebaseUser?.photoUrl
                findNavController().navigate(R.id.action_startFragment_to_todoFragment)
                writePersonalDataToSharedPref(uid, name, email, userImage)
                if (it.additionalUserInfo!!.isNewUser) {
                    Log.d(gTAG, "firebaseAuthWithGoogle: new acc created $email")
                } else {
                    Log.d(gTAG, "firebaseAuthWithGoogle: Existing user $email")
                }
            }
            .addOnFailureListener {
                Log.d(gTAG, "firebaseAuthWithGoogle: ${it.message}")
                Toast.makeText(
                    requireContext(),
                    "Login Failed due to ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun writePersonalDataToSharedPref(
        uid: String?,
        name: String?,
        email: String?,
        userImage: Uri?
    ): Boolean {
        if (uid!!.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_UID, uid)
            .putString(KEY_NAME, name)
            .putString(KEY_USER_IMAGE, userImage.toString())
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        print(sharedPref.getString(KEY_NAME, ""))
        return true
    }

    private fun updateUI(currentUser: FirebaseUser?, savedInstanceState: Bundle?) {
        if (currentUser != null) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.startFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_startFragment_to_todoFragment,
                savedInstanceState,
                navOptions
            )
        }
    }
}