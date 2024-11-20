/**
 * ScannerApplication.kt
 * Main application class that serves as the entry point for the Android application.
 * Implements Hilt for dependency injection and handles locale configuration.
 */
package com.licious.sample.scannersample

import android.app.Application
import android.content.Context
import com.licious.sample.design.ui.locale.LocaleHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScannerApplication : Application() {
    /**
     * Called when the application is starting, before any other application objects are created.
     * Overridden to apply locale settings to the base context.
     *
     * @param base The base context for the application
     */
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.applyLocaleToBaseContext(base))
    }
}