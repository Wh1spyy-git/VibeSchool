package ru.vibeschool.app.auth

import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SgoSessionStoreTest {
  private lateinit var store: SgoSessionStore

  @Before
  fun setUp() {
    store = SgoSessionStore(ApplicationProvider.getApplicationContext())
    store.clear()
  }

  @After
  fun tearDown() {
    store.clear()
  }

  @Test
  fun cookieRoundTripUsesPersistentEncryptedStore() {
    val cookie = "session=secret-value; tenant=22"

    store.saveCookie(cookie)

    assertTrue(store.hasSession.value)
    assertEquals(cookie, store.readCookie())
    store.clear()
    assertFalse(store.hasSession.value)
  }
}
