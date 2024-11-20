/**
 * MenuFragment.kt
 * Main menu fragment that serves as the navigation hub for the application.
 * Provides access to the scanner and settings functionalities.
 */
package com.licious.sample.scannersample.ui.menu

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.licious.sample.design.ui.base.BaseFragment
import com.licious.sample.scannersample.R
import com.licious.sample.scannersample.databinding.FragmentMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent

@AndroidEntryPoint
class MenuFragment : BaseFragment<FragmentMenuBinding>() {
    /**
     * Returns the tag used for logging purposes
     */
    override fun getLogTag(): String = TAG

    /**
     * Inflates and returns the view binding for this fragment
     */
    override fun getViewBinding(): FragmentMenuBinding =
        FragmentMenuBinding.inflate(layoutInflater)

    /**
     * Called after the view is created. Sets up the UI elements and their click listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * Initializes view components and sets up click listeners for navigation
     */
    private fun initView() {
        // Navigate to scanner when QR scan button is clicked
        binding.btnScanQr.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_scannerFragment)
        }

        // Launch settings activity when settings button is clicked
        binding.btnSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        // TODO: Add other buttons and their respective click handlers
    }

    companion object {
        private const val TAG = "MenuFragment"
    }
}