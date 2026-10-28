package ru.vibeschool.app.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SgoSessionStore(context: Context) {
  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
  private val _hasSession = MutableStateFlow(readCookie() != null)
  val hasSession: StateFlow<Boolean> = _hasSession.asStateFlow()

  fun saveCookie(cookie: String) {
    require(cookie.isNotBlank())
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
    val encrypted = cipher.doFinal(cookie.toByteArray(Charsets.UTF_8))
    preferences.edit {
      putString(KEY_COOKIE, Base64.encodeToString(encrypted, Base64.NO_WRAP))
      putString(KEY_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
      putLong(KEY_SAVED_AT, System.currentTimeMillis())
    }
    _hasSession.value = true
  }

  fun readCookie(): String? {
    val encrypted = preferences.getString(KEY_COOKIE, null) ?: return null
    val iv = preferences.getString(KEY_IV, null) ?: return null
    return runCatching {
      val cipher = Cipher.getInstance(TRANSFORMATION)
      cipher.init(
        Cipher.DECRYPT_MODE,
        getOrCreateKey(),
        GCMParameterSpec(GCM_TAG_LENGTH_BITS, Base64.decode(iv, Base64.NO_WRAP)),
      )
      cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP)).toString(Charsets.UTF_8)
    }.getOrNull()
  }

  fun clear() {
    preferences.edit { clear() }
    _hasSession.value = false
  }

  private fun getOrCreateKey(): SecretKey {
    val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
    (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
    return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER).run {
      init(
        KeyGenParameterSpec.Builder(
          KEY_ALIAS,
          KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
          .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
          .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
          .build(),
      )
      generateKey()
    }
  }

  private companion object {
    const val PREFERENCES_NAME = "sgo_session"
    const val KEY_COOKIE = "cookie"
    const val KEY_IV = "iv"
    const val KEY_SAVED_AT = "saved_at"
    const val KEY_ALIAS = "vibeschool_sgo_session"
    const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    const val TRANSFORMATION = "AES/GCM/NoPadding"
    const val GCM_TAG_LENGTH_BITS = 128
  }
}
