package ru.vibeschool.app.auth

import java.net.URI

object SgoAuthPolicy {
  const val BASE_URL = "https://netschool.edu22.info/"
  const val STUDENT_LOGIN_URL =
    "https://netschool.edu22.info/webapi/sso/esia/login?esia_permissions=1&esia_role=1"

  fun isAllowedUrl(url: String): Boolean {
    val uri = runCatching { URI(url) }.getOrNull() ?: return false
    if (uri.scheme != "https") return false
    val host = uri.host?.lowercase() ?: return false
    return host == "edu22.info" ||
      host.endsWith(".edu22.info") ||
      host == "gosuslugi.ru" ||
      host.endsWith(".gosuslugi.ru")
  }

  fun isDiaryUrl(url: String): Boolean {
    val uri = runCatching { URI(url) }.getOrNull() ?: return false
    return uri.scheme == "https" &&
      uri.host.equals("netschool.edu22.info", ignoreCase = true) &&
      uri.path.orEmpty().startsWith("/app/diary")
  }
}
