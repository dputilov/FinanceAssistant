package com.arkub.unified.api.apicoordinator

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class LollipopEncryptedStorage constructor(context: Context) : EncryptedStorage {

    private val sharedPreferences: SharedPreferences

    private val apiEncryptedStorageName = "LollipopApiEncryptedStoragePrefs"

    init
    {
        sharedPreferences = context.getSharedPreferences(apiEncryptedStorageName, MODE_PRIVATE);
    }

    override fun setValue(key: String, value: String?) {
        if (value == null){
            sharedPreferences
                    .edit()
                    .remove(key)
                    .apply()
        } else {
            sharedPreferences
                    .edit()
                    .putString(key, value)
                    .apply()
        }
    }

    override fun getValue(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun getSharedPreferences() : SharedPreferences {
        return sharedPreferences
    }
}