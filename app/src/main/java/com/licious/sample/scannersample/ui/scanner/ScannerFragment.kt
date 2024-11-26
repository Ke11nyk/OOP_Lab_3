/**
 * ScannerFragment.kt
 * Fragment that implements the QR code scanning functionality.
 * Handles camera preview, QR code detection, and provides feedback on successful scans.
 */
package com.licious.sample.scannersample.ui.scanner

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.licious.sample.design.ui.base.BaseFragment
import com.licious.sample.scannersample.databinding.FragmentScannerBinding
import com.licious.sample.scannersample.ui.scanner.viewmodels.ScannerViewModel
import com.licious.sample.scanner.ScannerViewState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ScannerFragment : BaseFragment<FragmentScannerBinding>() {
    // ViewModel for handling scanner logic
    private val qrCodeViewModel: ScannerViewModel by viewModels()

    // Vibrator service for haptic feedback
    private val vibrator: Vibrator by lazy {
        requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun getLogTag(): String = TAG

    override fun getViewBinding(): FragmentScannerBinding =
        FragmentScannerBinding.inflate(layoutInflater)

    /**
     * Initializes the scanner view and starts scanning animation
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        startAnimation()
    }

    /**
     * Cleanup vibrator when view is destroyed
     */
    override fun onDestroyView() {
        vibrator.cancel()
        super.onDestroyView()
    }

    /**
     * Initializes the camera preview and sets up QR code detection
     */
    private fun initView() {
        qrCodeViewModel.startCamera(viewLifecycleOwner, requireContext(), binding.previewView, ::onResult)
    }

    /**
     * Handles the result of QR code scanning
     * Provides feedback through vibration and toast messages
     *
     * @param state Current state of the scanner
     * @param result Scanned QR code result or error message
     */
    private fun onResult(state: ScannerViewState, result: String?) {
        Timber.d("Scan result - State: %s, Result: %s", state, result)
        when (state) {
            ScannerViewState.Success -> {
                vibrateOnScan()
                result?.let {
                    Timber.i("Successfully scanned QR code: %s", it)
                    val intent = Intent(requireContext(), LinkDisplayActivity::class.java)
                    intent.putExtra("LINK", it)
                    startActivity(intent)
                }
            }
            ScannerViewState.Error -> {
                Timber.e("Scan error: %s", result)
                Toast.makeText(requireContext(), "Error: $result", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Timber.w("Unknown scan state: %s", result)
                Toast.makeText(requireContext(), "Unknown error: $result", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Starts the scanning animation for the red bar
     */
    private fun startAnimation() {
        val animation: Animation = AnimationUtils.loadAnimation(context, com.licious.sample.scanner.R.anim.barcode_animator)
        binding.llAnimation.startAnimation(animation)
    }

    /**
     * Provides haptic feedback when QR code is successfully scanned
     * Handles different Android versions for vibration implementation
     */
    private fun vibrateOnScan() {
        try {
            Timber.d("Vibrating on successful scan")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        VIBRATE_DURATION,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(VIBRATE_DURATION)
            }
        } catch (e: Exception) {
            Timber.e(e, "Vibration failed")
        }
    }

    companion object {
        private const val TAG = "QrCodeReaderFragment"
        private const val VIBRATE_DURATION = 200L  // Duration of vibration feedback in milliseconds
    }
}