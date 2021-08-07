package com.raveendran.helpdevs.other

import android.content.Context
import android.content.SharedPreferences
import com.raveendran.helpdevs.other.Constants.SHARED_PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext

class SharedPrefs {

    companion object {
        fun sharedPreferences(@ApplicationContext app: Context): SharedPreferences =
            app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        fun clearSharedPrefs(sharedPref: SharedPreferences) {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()
        }
    }

}