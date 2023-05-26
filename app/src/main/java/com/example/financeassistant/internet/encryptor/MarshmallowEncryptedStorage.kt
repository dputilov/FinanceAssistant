package com.arkub.unified.api.apicoordinator

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class MarshmallowEncryptedStorage constructor(private val context: Context) : EncryptedStorage {

    private val sharedPreferences: SharedPreferences

    private val apiEncryptedStorageName = "ApiEncryptedStoragePrefs"

    init
    {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        sharedPreferences = EncryptedSharedPreferences.create(
                apiEncryptedStorageName,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
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