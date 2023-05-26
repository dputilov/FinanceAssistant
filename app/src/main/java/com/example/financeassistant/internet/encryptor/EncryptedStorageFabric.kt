package com.arenberg.eye.data.encryptor

import android.content.Context
import android.os.Build
import com.arkub.unified.api.apicoordinator.EncryptedStorage
import com.arkub.unified.api.apicoordinator.LollipopEncryptedStorage
import com.arkub.unified.api.apicoordinator.MarshmallowEncryptedStorage

class EncryptedStorageFabric {

    companion object {
        fun getEncryptedStorage(context: Context): EncryptedStorage {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return MarshmallowEncryptedStorage(context)
            }
            return LollipopEncryptedStorage(context)
        }
    }
}