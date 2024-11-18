package com.licious.sample.scannersample.ui.menu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.licious.sample.scannersample.R
import com.licious.sample.scannersample.databinding.ActivitySettingsBinding
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialization Binding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialization SharedPreferences
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // Set button text for language selection
        binding.btnSelectLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = resources.getStringArray(R.array.languages)
        val languageCodes = resources.getStringArray(R.array.language_codes)

        AlertDialog.Builder(this)
            .setTitle(R.string.select_language)
            .setItems(languages) { _, which ->
                val selectedLanguageCode = languageCodes[which]
                setLocale(selectedLanguageCode)
            }
            .create()
            .show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save choice of the user
        sharedPreferences.edit().putString("selected_language", languageCode).apply()

        // Message about successful language change
        Toast.makeText(this, R.string.language_changed, Toast.LENGTH_SHORT).show()

        // Restart activity to apply changes
        recreate()
    }
}
