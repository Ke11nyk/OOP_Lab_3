package com.licious.sample.scannersample.ui.scanner

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.licious.sample.scannersample.R

class LinkDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_display)

        val link = intent.getStringExtra("LINK")

        val linkTextView: TextView = findViewById(R.id.linkTextView)
        linkTextView.text = link

        // Додайте можливість натискання на посилання
        linkTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse(link)
            }
            startActivity(browserIntent)
        }
    }
}
