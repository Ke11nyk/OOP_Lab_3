/**
 * LocaleHelper.kt
 * Helper object for managing application localization/internationalization.
 * Provides functionality to set, persist, and retrieve locale settings across app restarts.
 */
package com.licious.sample.design.ui.locale

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {
    // Constants for SharedPreferences
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    /**
     * Sets the application locale to the specified language code and persists the selection.
     *
     * @param context The application or activity context
     * @param languageCode The ISO 639-1 language code (e.g., "en" for English, "es" for Spanish)
     * @return A new Context object with the updated locale configuration
     */
    fun setLocale(context: Context, languageCode: String): Context {
        // Save the selected language preference
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()

        // Update and return the localized context
        return updateResourcesLocale(context, languageCode)
    }

    /**
     * Retrieves the previously selected language code from SharedPreferences.
     * Falls back to system default language if no preference is saved.
     *
     * @param context The application or activity context
     * @return The stored language code or device default language code
     */
    fun getPersistedLocale(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, Locale.getDefault().language) ?: Locale.getDefault().language
    }

    /**
     * Updates the context's resources configuration with the specified language.
     * Handles different implementation methods based on Android API level.
     *
     * @param context The context to be updated
     * @param language The language code to apply
     * @return A new Context object with the updated configuration
     */
    private fun updateResourcesLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        return when {
            // For Android N (API 24) and above
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                configuration.setLocale(locale)
                context.createConfigurationContext(configuration)
            }
            // For Android Jelly Bean MR1 (API 17) to Android M (API 23)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
                configuration.setLocale(locale)
                context.createConfigurationContext(configuration)
            }
            // For Android versions below Jelly Bean MR1 (API 17)
            else -> {
                @Suppress("DEPRECATION")
                configuration.locale = locale
                @Suppress("DEPRECATION")
                resources.updateConfiguration(configuration, resources.displayMetrics)
                context
            }
        }
    }

    /**
     * Applies the persisted locale settings to the base context.
     * Typically used in Activity.attachBaseContext() or Application.attachBaseContext().
     *
     * @param base The base context to be updated
     * @return A new Context object with the persisted locale configuration
     */
    fun applyLocaleToBaseContext(base: Context): Context {
        val persistedLocale = getPersistedLocale(base)
        return setLocale(base, persistedLocale)
    }
}