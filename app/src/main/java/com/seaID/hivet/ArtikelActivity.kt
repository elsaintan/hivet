package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class ArtikelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artikel)

        val mArtikel : WebView = findViewById<View>(R.id.artikel) as WebView
        mArtikel.loadUrl("http://hivetcare.my.id/posts")

        val webSettings : WebSettings = mArtikel.settings
        webSettings.javaScriptEnabled = true
        mArtikel.webViewClient = WebViewClient()
        
        mArtikel.canGoBack()
        mArtikel.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP && mArtikel.canGoBack()){
                mArtikel.goBack()
                return@OnKeyListener true
            }
            false
        })

    }
}