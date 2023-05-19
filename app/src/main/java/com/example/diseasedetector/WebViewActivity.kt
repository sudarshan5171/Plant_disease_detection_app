package com.example.diseasedetector

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.diseasedetector.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private var _binding: ActivityWebViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val webView = binding.webView
        supportActionBar?.hide()
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        val url = intent.extras?.getString(DATA_URL)
        url?.let {
            webView.loadUrl(url)
        }
    }

    companion object {
        const val DATA_URL = "data_url"
        const val HIDE_TOOLBAR = "hide_toolbar"
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}