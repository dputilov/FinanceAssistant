package com.arkub.unified.api.apicoordinator

import android.content.SharedPreferences

interface EncryptedStorage
{

    fun setValue(key: String, value: String?)

    fun getValue(key: String): String?

    fun getSharedPreferences() : SharedPreferences

}