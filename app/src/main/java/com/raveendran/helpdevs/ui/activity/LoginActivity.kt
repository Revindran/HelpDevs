package com.raveendran.helpdevs.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val gTAG = "Google Sign In"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_login)

        sharedPref = this.let { SharedPrefs.sharedPreferences(it) }!!
        auth = Firebase.auth
        val currentUser = auth.currentUser
        updateUI(currentUser)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

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
                navigateToHome()
                writePersonalDataToSharedPref(uid, name, email, userImage)
                if (it.additionalUserInfo!!.isNewUser) {
                    Log.d(gTAG, "firebaseAuthWithGoogle: new acc created $email")
                } else {
                    Log.d(gTAG, "firebaseAuthWithGoogle: Existing user $email")
                }
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d(gTAG, "firebaseAuthWithGoogle: ${it.message}")
                Toast.makeText(
                    this,
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
            .putString(Constants.KEY_UID, uid)
            .putString(Constants.KEY_NAME, name)
            .putString(Constants.KEY_USER_IMAGE, userImage.toString())
            .putString(Constants.KEY_EMAIL, email)
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        print(sharedPref.getString(Constants.KEY_NAME, ""))
        return true
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}