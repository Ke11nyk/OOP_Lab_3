/**
 * LinkDisplayActivity.kt
 * An activity that displays and handles interaction with QR code scan results.
 * Allows users to view the scanned link and open it in a browser.
 */
package com.licious.sample.scannersample.ui.scanner

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.licious.sample.design.ui.base.BaseActivity
import com.licious.sample.scannersample.R
import com.licious.sample.scannersample.databinding.ActivityLinkDisplayBinding
import com.licious.sample.scannersample.databinding.ActivitySettingsBinding
import timber.log.Timber

class LinkDisplayActivity : BaseActivity<ActivityLinkDisplayBinding>() {
    // Tag used for logging purposes
    override fun getLogTag(): String = TAG

    // Inflates the activity layout using view binding
    override fun getViewBinding(): ActivityLinkDisplayBinding =
        ActivityLinkDisplayBinding.inflate(layoutInflater)

    /**
     * Initializes the activity and sets up the UI components.
     * Retrieves the scanned link from the intent and displays it.
     * Sets up click handling to open the link in a browser.
     *
     * @param savedInstanceState If the activity is being re-initialized after being shut down,
     *                          this contains the most recent data, otherwise null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_display)

        val link = intent.getStringExtra("LINK")
        Timber.i("Received link: %s", link)

        val linkTextView: TextView = findViewById(R.id.linkTextView)
        linkTextView.text = link

        linkTextView.setOnClickListener {
            Timber.d("Opening link in browser: %s", link)
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse(link)
            }
            startActivity(browserIntent)
        }
    }

    companion object {
        private const val TAG = "LinkDisplayActivity"
    }
}