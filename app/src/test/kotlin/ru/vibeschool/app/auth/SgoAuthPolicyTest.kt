package ru.vibeschool.app.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SgoAuthPolicyTest {
  @Test
  fun `allows only HTTPS SGO and Gosuslugi hosts`() {
    assertTrue(SgoAuthPolicy.isAllowedUrl("https://netschool.edu22.info/webapi/sso/esia/login"))
    assertTrue(SgoAuthPolicy.isAllowedUrl("https://esia-new.edu22.info/api/v1/connect"))
    assertTrue(SgoAuthPolicy.isAllowedUrl("https://esia.gosuslugi.ru/login"))
    assertFalse(SgoAuthPolicy.isAllowedUrl("http://netschool.edu22.info/"))
    assertFalse(SgoAuthPolicy.isAllowedUrl("https://gosuslugi.ru.evil.example/login"))
  }

  @Test
  fun `recognizes diary return URL`() {
    assertTrue(SgoAuthPolicy.isDiaryUrl("https://netschool.edu22.info/app/diary/dashboard"))
    assertFalse(SgoAuthPolicy.isDiaryUrl("https://netschool.edu22.info/"))
    assertFalse(SgoAuthPolicy.isDiaryUrl("https://evil.example/app/diary/dashboard"))
  }
}
