/**
 * ScannerAnalyzer.kt
 * Image analyzer class that processes camera frames to detect and decode barcodes/QR codes
 * using ML Kit Vision API.
 *
 * @param onResult Callback function to deliver scan results with state and decoded value
 */
package com.licious.sample.scanner

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScannerAnalyzer(
    private val onResult: (state: ScannerViewState, barcode: String) -> Unit
) : ImageAnalysis.Analyzer {

    // Delay between processing consecutive frames to avoid excessive CPU usage
    private val delayForProcessingNextImage = 300L

    /**
     * Processes each frame from the camera to detect and decode barcodes
     * @param imageProxy Container for the camera frame data
     */
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        // Configure barcode scanner to detect all supported formats
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        val scanner = BarcodeScanning.getClient(options)

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                .let { image ->
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                onResult(ScannerViewState.Success, barcode.rawValue ?: "")
                            }
                        }
                        .addOnFailureListener {
                            onResult(ScannerViewState.Error, it.message.toString())
                        }
                        .addOnCompleteListener {
                            // Delay before processing next frame and release resources
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(delayForProcessingNextImage)
                                imageProxy.close()
                            }
                        }
                }
        } else {
            onResult(ScannerViewState.Error, "Image is empty")
        }
    }
}