package com.example.nfcsmartcard.utils

import android.content.Context
import com.example.nfcsmartcard.data.network.model.user.User
import com.example.nfcsmartcard.utils.Constants.PREFS_BUS_NUMBER_KEY
import com.example.nfcsmartcard.utils.Constants.PREFS_ROUTE_CATEGORY_KEY
import com.example.nfcsmartcard.utils.Constants.PREFS_TOKEN_FILE
import com.example.nfcsmartcard.utils.Constants.PREFS_TOKEN_KEY
import com.example.nfcsmartcard.utils.Constants.PREFS_USERNAME_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveTokenAndUser(token: String, user: User) {
        val prefEditor = prefs.edit()
        // saving token
        prefEditor.putString(PREFS_TOKEN_KEY, token)

        // saving userDetails
        prefEditor.putString(PREFS_USERNAME_KEY, user.username)
        prefEditor.putString(PREFS_ROUTE_CATEGORY_KEY, user.route_category)
        prefEditor.putString(PREFS_BUS_NUMBER_KEY, user.bus_number.toString())
        prefEditor.apply()
    }

    fun getToken(): String? {
        // 'null' is default value here
        return prefs.getString(PREFS_TOKEN_KEY, null)
    }

    fun getUserDetails(): List<String?> {
        // [0] : username
        // [1] : route category
        // [2] : bus number
        return arrayListOf<String?>(
            prefs.getString(PREFS_USERNAME_KEY, null),
            prefs.getString(PREFS_ROUTE_CATEGORY_KEY, null),
            prefs.getString(PREFS_BUS_NUMBER_KEY, null)
        )
    }

    fun clearTokenAndUser() {
        prefs.edit().clear().apply()
    }
}