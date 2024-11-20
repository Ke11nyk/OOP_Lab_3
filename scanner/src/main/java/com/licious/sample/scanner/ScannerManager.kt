/**
 * ScannerManager.kt
 * Manager class that extends BaseCameraManager to implement barcode/QR code scanning functionality.
 * Configures and binds camera use cases specifically for scanning purposes.
 *
 * @param owner Lifecycle owner for camera operations
 * @param context Android context
 * @param viewPreview Preview view to display camera feed
 * @param onResult Callback to deliver scan results
 * @param lensFacing Initial camera lens direction
 */
package com.licious.sample.scanner

import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.licious.sample.scanner.base.BaseCameraManager

class ScannerManager(
    owner: LifecycleOwner,
    context: Context,
    viewPreview: PreviewView,
    private val onResult: (state: ScannerViewState, result: String) -> Unit,
    lensFacing: Int
) : BaseCameraManager(owner, context, viewPreview, lensFacing, {}) {

    /**
     * Creates and configures image analysis use case for barcode scanning
     * @return Configured ImageAnalysis instance with attached analyzer
     */
    private fun getImageAnalysis(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, ScannerAnalyzer(onResult))
            }
    }

    /**
     * Implements abstract method from BaseCameraManager to bind camera use cases
     * Adds image analysis use case specifically for scanning
     */
    override fun bindToLifecycle(
        cameraProvider: ProcessCameraProvider,
        owner: LifecycleOwner,
        cameraSelector: CameraSelector,
        previewView: Preview,
        imageCapture: ImageCapture
    ) {
        camera = cameraProvider.bindToLifecycle(
            owner,
            cameraSelector,
            previewView,
            getImageAnalysis(),
            imageCapture
        )
    }
}