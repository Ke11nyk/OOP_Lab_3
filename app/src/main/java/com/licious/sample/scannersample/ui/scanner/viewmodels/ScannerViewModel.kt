/**
 * ScannerViewModel.kt
 * ViewModel responsible for managing QR code scanning business logic and camera operations.
 * Uses Hilt for dependency injection and manages the scanner's lifecycle.
 */
package com.licious.sample.scannersample.ui.scanner.viewmodels

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.licious.sample.scanner.ScannerManager
import com.licious.sample.scanner.ScannerViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class ScannerViewModel @Inject constructor(): ViewModel() {
    // Manager instance handling camera and scanning operations
    private lateinit var qrCodeManager: ScannerManager

    /**
     * Initializes and starts the camera for QR code scanning
     *
     * @param viewLifecycleOwner Lifecycle owner for camera operations
     * @param context Application context for camera initialization
     * @param previewView View for displaying camera preview
     * @param onResult Callback function for handling scan results/errors
     */
    internal fun startCamera(
        viewLifecycleOwner: LifecycleOwner,
        context: Context,
        previewView: PreviewView,
        onResult: (state: ScannerViewState, result: String) -> Unit,
    ) {
        Timber.d("Starting camera with back lens")
        qrCodeManager = ScannerManager(
            owner = viewLifecycleOwner,
            context = context,
            viewPreview = previewView,
            onResult = onResult,
            lensFacing = CameraSelector.LENS_FACING_BACK
        )
    }
}