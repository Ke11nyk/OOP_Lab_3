/**
 * SettingsActivity.kt
 * Activity that handles application settings, primarily focusing on language selection.
 * Implements locale changing functionality with application restart capability.
 */
package com.licious.sample.scannersample.ui.menu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.licious.sample.design.ui.base.BaseActivity
import com.licious.sample.design.ui.locale.LocaleHelper
import com.licious.sample.scannersample.R
import com.licious.sample.scannersample.databinding.ActivitySettingsBinding
import timber.log.Timber

class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {
    // SharedPreferences instance to store application preferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun getLogTag(): String = TAG

    override fun getViewBinding(): ActivitySettingsBinding =
        ActivitySettingsBinding.inflate(layoutInflater)

    /**
     * Initializes the activity, sets up SharedPreferences and click listeners
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("onCreate called")

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        binding.btnSelectLanguage.setOnClickListener {
            Timber.d("Language selection button clicked")
            showLanguageSelectionDialog()
        }
    }

    /**
     * Displays a dialog for language selection
     * Allows user to choose from available languages and applies the selected locale
     */
    private fun showLanguageSelectionDialog() {
        val languages = resources.getStringArray(R.array.languages)
        val languageCodes = resources.getStringArray(R.array.language_codes)

        Timber.d("Showing language selection dialog")
        AlertDialog.Builder(this)
            .setTitle(R.string.select_language)
            .setItems(languages) { _, which ->
                val selectedLanguageCode = languageCodes[which]

                Timber.i("Language selected: $selectedLanguageCode")
                LocaleHelper.setLocale(this, selectedLanguageCode)
                restartApplication()
            }
            .create()
            .show()
    }

    /**
     * Restarts the application to apply locale changes
     * Clears the activity stack and starts fresh with the new locale
     */
    private fun restartApplication() {
        Timber.d("Restarting application to apply language changes")
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "SettingsActivity"
    }
}
