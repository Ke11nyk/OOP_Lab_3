/**
 * IGetPermissionListener.kt
 * Interface defining callback methods for handling permission request results.
 * Implements the observer pattern for permission state changes.
 */
package com.licious.sample.design.ui.permission

interface IGetPermissionListener {
    /**
     * Called when the requested permission is granted by the user
     */
    fun onPermissionGranted()

    /**
     * Called when the requested permission is denied by the user
     */
    fun onPermissionDenied()

    /**
     * Called when the permission rationale should be shown to the user
     * Typically triggered when permission was previously denied but not permanently
     */
    fun onPermissionRationale()
}