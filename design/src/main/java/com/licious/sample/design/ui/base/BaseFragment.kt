/**
 * BaseFragment.kt
 * Abstract base fragment class providing common functionality for all fragments.
 * Implements ViewBinding for view management.
 *
 * @param VB ViewBinding type parameter for the fragment
 */
package com.licious.sample.design.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    /**
     * @return Tag string used for logging purposes
     */
    abstract fun getLogTag(): String

    /**
     * Lazy-initialized view binding instance
     * Ensures binding is only created when needed
     */
    val binding: VB by lazy {
        getViewBinding()
    }

    /**
     * Creates and returns the fragment's view hierarchy
     * Uses ViewBinding to inflate the layout
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    /**
     * @return Instance of ViewBinding for this fragment
     */
    abstract fun getViewBinding(): VB
}