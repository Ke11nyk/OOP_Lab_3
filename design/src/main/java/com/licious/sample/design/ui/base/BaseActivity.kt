/**
 * BaseActivity.kt
 * Abstract base activity class providing common functionality for all activities.
 * Implements ViewBinding and locale management.
 *
 * @param VB ViewBinding type parameter for the activity
 */
package com.licious.sample.design.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.licious.sample.design.ui.locale.LocaleHelper

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    /**
     * Lazy-initialized view binding instance
     * Ensures binding is only created when needed
     */
    val binding: VB by lazy {
        getViewBinding()
    }

    /**
     * Applies locale settings when activity context is attached
     * Ensures consistent localization across the application
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocaleToBaseContext(newBase))
    }

    /**
     * Sets up the activity's content view using ViewBinding
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    /**
     * @return Tag string used for logging purposes
     */
    abstract fun getLogTag(): String

    /**
     * @return Instance of ViewBinding for this activity
     */
    abstract fun getViewBinding(): VB
}