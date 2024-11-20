/**
 * BaseCameraManager.kt
 * Base class that handles camera functionality including initialization, lifecycle management,
 * and camera operations like switching between front/back cameras and flash control.
 *
 * @param owner The lifecycle owner to bind camera operations
 * @param context Android context for accessing system services
 * @param viewPreview PreviewView to display camera feed
 * @param lensFacing Initial camera lens direction (front/back)
 * @param showHideFlashIcon Callback to control flash icon visibility based on camera facing
 */
package com.licious.sample.scanner.base

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseCameraManager(
    private val owner: LifecycleOwner,
    private val context: Context,
    private val viewPreview: PreviewView,
    private var lensFacing: Int,
    private val showHideFlashIcon: (show: Int) -> Unit
) : DefaultLifecycleObserver {
    // Image capture use case for taking photos
    private var imgCapture: ImageCapture? = null
    // Camera provider that manages camera lifecycle
    private lateinit var cameraProvider: ProcessCameraProvider
    // Flag to track if camera was stopped
    private var stopped: Boolean = false
    // Current camera instance
    protected var camera: Camera? = null
    // Current flash mode setting
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF

    /**
     * Single thread executor for camera operations to ensure sequential processing
     */
    protected val cameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    init {
        // Register lifecycle observer and initialize camera
        owner.lifecycle.addObserver(this)
        startCamera()
    }

    /**
     * Initializes and starts the camera with specified configuration
     * @param isSwitchButtonClicked Flag to indicate if this is called from camera switch action
     */
    private fun startCamera(isSwitchButtonClicked: Boolean = false) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            controlWhichCameraToDisplay(isSwitchButtonClicked)
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Controls which camera to display and handles switching between front/back cameras
     * @param isSwitchButtonClicked Flag to determine if camera switch was requested
     * @return Selected lens facing direction
     */
    private fun controlWhichCameraToDisplay(isSwitchButtonClicked: Boolean): Int {
        if (isSwitchButtonClicked) {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT)
                CameraSelector.LENS_FACING_BACK
            else
                CameraSelector.LENS_FACING_FRONT
        }
        showHideFlashIcon(lensFacing)
        return lensFacing
    }

    /**
     * Binds camera use cases (preview, image capture) to the lifecycle owner
     */
    private fun bindCameraUseCases() {
        val cameraSelector = getCameraSelector()
        val previewView = getPreviewUseCase()
        imgCapture = getImageCapture()
        cameraProvider.unbindAll()
        try {
            imgCapture?.let {
                bindToLifecycle(cameraProvider, owner, cameraSelector, previewView, it)
            }
            previewView.setSurfaceProvider(viewPreview.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(ContentValues.TAG, "Use case binding failed $exc")
        }
    }

    /**
     * Lifecycle method to handle camera pause
     * Unbinds all use cases and marks camera as stopped
     */
    override fun onPause(owner: LifecycleOwner) {
        if (this::cameraProvider.isInitialized) {
            cameraProvider.unbindAll()
            stopped = true
            super.onPause(owner)
        }
    }

    /**
     * Lifecycle method to handle camera resume
     * Rebinds camera use cases if camera was previously stopped
     */
    override fun onResume(owner: LifecycleOwner) {
        if (stopped) {
            bindCameraUseCases()
            stopped = false
        }
        super.onResume(owner)
    }

    /**
     * Lifecycle method to clean up resources
     * Shuts down camera executor service
     */
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cameraExecutor.shutdown()
    }

    /**
     * Abstract method to be implemented by subclasses for binding specific camera use cases
     */
    protected abstract fun bindToLifecycle(
        cameraProvider: ProcessCameraProvider,
        owner: LifecycleOwner,
        cameraSelector: CameraSelector,
        previewView: Preview,
        imageCapture: ImageCapture
    )

    /**
     * Creates and configures camera selector for specified lens facing
     */
    private fun getCameraSelector(): CameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    /**
     * Creates and configures preview use case
     */
    private fun getPreviewUseCase(): Preview = Preview.Builder()
        .build()

    /**
     * Creates and configures image capture use case with current flash mode
     */
    private fun getImageCapture(): ImageCapture = ImageCapture.Builder()
        .setFlashMode(flashMode)
        .build()

    /**
     * Controls torch/flashlight for QR code scanning
     * @param onFlashMode True to enable flash, false to disable
     */
    fun enableFlashForQrCode(onFlashMode: Boolean) {
        camera?.cameraControl?.enableTorch(onFlashMode)
    }

    /**
     * Controls camera flash for photo capture
     * @param flashStatus True to enable flash, false to disable
     */
    fun enableFlashForCamera(flashStatus: Boolean) {
        flashMode = if (flashStatus)
            ImageCapture.FLASH_MODE_ON
        else
            ImageCapture.FLASH_MODE_OFF
        imgCapture?.flashMode = flashMode
    }
}