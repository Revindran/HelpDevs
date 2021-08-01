package com.raveendran.helpdevs.other

import android.content.Context
import android.content.SharedPreferences
import com.raveendran.helpdevs.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.raveendran.helpdevs.other.Constants.KEY_NAME
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

        fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

        fun provideFirstTimeToggle(sharedPref: SharedPreferences) =
            sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
    }

}