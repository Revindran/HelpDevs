package com.raveendran.helpdevs.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.raveendran.helpdevs.R
import kotlinx.android.synthetic.main.dev_webview_fragment.*

class DevWebViewFragment : Fragment(R.layout.dev_webview_fragment) {

    private val args: DevWebViewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dev = args.devData

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(dev.url)
        }
    }
}