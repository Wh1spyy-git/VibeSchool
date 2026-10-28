package ru.vibeschool.app.auth

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ru.vibeschool.app.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled") // The official SGO and ESIA login flows require JavaScript.
@Composable
fun SgoAuthScreen(
  initialCookie: String?,
  onSessionCaptured: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var webView by remember { mutableStateOf<WebView?>(null) }
  var progress by remember { mutableIntStateOf(0) }
  var isLoading by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  BackHandler {
    val currentWebView = webView
    if (currentWebView?.canGoBack() == true) currentWebView.goBack() else onBack()
  }

  Scaffold(
    modifier = modifier,
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.auth_title)) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              Icons.AutoMirrored.Outlined.ArrowBack,
              contentDescription = stringResource(R.string.common_back),
            )
          }
        },
      )
    },
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
          var authCompleted = false
          WebView(context).apply {
            settings.apply {
              javaScriptEnabled = true
              domStorageEnabled = true
              allowFileAccess = false
              allowContentAccess = false
              mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
              setSupportMultipleWindows(false)
              javaScriptCanOpenWindowsAutomatically = false
              userAgentString = "$userAgentString VibeSchool/1.2"
            }
            val currentWebView = this
            CookieManager.getInstance().apply {
              setAcceptCookie(true)
              setAcceptThirdPartyCookies(currentWebView, true)
              initialCookie
                ?.split(';')
                ?.map(String::trim)
                ?.filter(String::isNotEmpty)
                ?.forEach { cookie -> setCookie(SgoAuthPolicy.BASE_URL, cookie) }
              flush()
            }
            webChromeClient = object : WebChromeClient() {
              override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress = newProgress
                isLoading = newProgress < 100
              }
            }
            webViewClient = object : WebViewClient() {
              override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest,
              ): Boolean {
                val url = request.url.toString()
                if (SgoAuthPolicy.isAllowedUrl(url)) return false
                if (request.isForMainFrame) {
                  errorMessage = context.getString(R.string.auth_blocked_address)
                }
                return true
              }

              override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                errorMessage = null
              }

              override fun onPageFinished(view: WebView?, url: String?) {
                val finishedUrl = url ?: return
                if (authCompleted || !SgoAuthPolicy.isDiaryUrl(finishedUrl)) return
                val cookieManager = CookieManager.getInstance()
                val cookie = cookieManager.getCookie(finishedUrl)
                  ?: cookieManager.getCookie(SgoAuthPolicy.BASE_URL)
                if (cookie.isNullOrBlank()) {
                  errorMessage = context.getString(R.string.auth_session_missing)
                  return
                }
                authCompleted = true
                cookieManager.flush()
                onSessionCaptured(cookie)
              }
            }
            loadUrl(SgoAuthPolicy.STUDENT_LOGIN_URL)
            webView = this
          }
        },
        onRelease = { releasedWebView ->
          webView = null
          releasedWebView.stopLoading()
          releasedWebView.webChromeClient = null
          releasedWebView.webViewClient = WebViewClient()
          releasedWebView.destroy()
        },
      )
      if (isLoading) {
        LinearProgressIndicator(
          progress = { progress / 100f },
          modifier = Modifier.align(Alignment.TopCenter),
        )
      }
      if (progress == 0) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
      }
      errorMessage?.let { message ->
        Text(
          text = message,
          modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
}
