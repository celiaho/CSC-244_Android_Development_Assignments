package edu.bhcc.cho.note_takingserverapp.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

fun getEncryptedSharedPreferences(context: Context, fileName: String): SharedPreferences? {
    val masterKeyAlias = "MASTER_KEY_ALIAS"

    // Check for SDK version for backwards compatibility
    val keyGenParameterSpec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        KeyGenParameterSpec.Builder(
            masterKeyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
    } else {
        // For older SDK versions, use a less secure but still acceptable method
        KeyGenParameterSpec.Builder(masterKeyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setKeySize(256)
            .build()
    }

    val masterKey = try {
        MasterKey.Builder(context)
            .setKeyGenParameterSpec(keyGenParameterSpec)
            .build()
    } catch (e: GeneralSecurityException) {
        e.printStackTrace()
        return null
    }

    return try {
        EncryptedSharedPreferences.create(
            fileName,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.KTS_1_0,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.KTS_1_0
        )
    } catch (e: GeneralSecurityException) {
        e.printStackTrace()
        null
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}