/**
 * PermissionUtil.kt
 * Utility class for handling Android runtime permissions.
 * Provides methods for checking, requesting, and managing both single and multiple permissions.
 */
package com.licious.sample.design.ui.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import timber.log.Timber

class PermissionUtil {
    // Stores the current permission being processed
    private var permissionName: String = ""
    // Listener for permission-related callbacks
    private var permissionListener: IGetPermissionListener? = null

    /**
     * Handles the result of a single permission request
     *
     * @param activity Current activity context
     * @param isGranted Whether the permission was granted
     */
    fun handleSinglePermissionResult(activity: AppCompatActivity, isGranted: Boolean) {
        Timber.d("Single Permission Result - $permissionName: ${if (isGranted) "Granted" else "Denied"}")
        when {
            isGranted -> {
                permissionListener?.onPermissionGranted()
            }
            isShouldShowRequestPermissionRationale(activity, permissionName) -> {
                Timber.i("Permission Rationale Needed: $permissionName")
                permissionListener?.onPermissionRationale()
            }
            else -> {
                Timber.w("Permission Permanently Denied: $permissionName")
                permissionListener?.onPermissionDenied()
            }
        }
    }

    /**
     * Registers a listener for permission-related callbacks
     *
     * @param permissionListener Listener implementation to receive callbacks
     */
    fun setPermissionListener(permissionListener: IGetPermissionListener) {
        this.permissionListener = permissionListener
        Timber.d("Permission listener set")
    }

    /**
     * Handles the result of multiple permission requests
     *
     * @param activity Current activity context
     * @param permissions Map of permissions and their grant results
     */
    fun handleMultiPermissionResult(
        activity: AppCompatActivity,
        permissions: Map<String, @JvmSuppressWildcards Boolean>
    ) {
        Timber.d("Multi-Permission Result Processing")
        var isGranted = true
        permissions.entries.forEach {
            Timber.v("Permission ${it.key}: ${if (it.value) "Granted" else "Denied"}")
            if (!it.value) {
                isGranted = false
                return@forEach
            }
        }

        when {
            isGranted -> {
                Timber.i("All Permissions Granted")
                permissionListener?.onPermissionGranted()
            }
            isShouldShowRequestPermissionRationale(activity, permissions) -> {
                Timber.i("Permission Rationale Needed")
                permissionListener?.onPermissionRationale()
            }
            else -> {
                Timber.w("Permissions Denied")
                permissionListener?.onPermissionDenied()
            }
        }
    }

    /**
     * Checks if a single permission is granted
     *
     * @param context Application context
     * @param permissionName Permission to check
     * @return true if permission is granted, false otherwise
     */
    fun hasPermission(context: Context, permissionName: String): Boolean {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            permissionName
        ) == PackageManager.PERMISSION_GRANTED
        Timber.v("Permission Check - $permissionName: ${if (isGranted) "Granted" else "Denied"}")
        return isGranted
    }

    /**
     * Checks if multiple permissions are granted
     *
     * @param context Application context
     * @param permissions Array of permissions to check
     * @return true if all permissions are granted, false otherwise
     */
    fun hasMultiPermissions(context: Context, permissions: Array<String>): Boolean {
        permissions.forEach {
            if (!hasPermission(context, it)) {
                Timber.d("Multi-Permission Check: Not all permissions granted")
                return false
            }
        }
        Timber.d("Multi-Permission Check: All permissions granted")
        return true
    }

    /**
     * Requests a single permission
     *
     * @param permission Permission to request
     * @param requestPermissions Launcher for permission request
     */
    fun requestPermission(permission: String, requestPermissions: ActivityResultLauncher<String>) {
        permissionName = permission
        Timber.d("Requesting Permission: $permission")
        requestPermissions.launch(permission)
    }

    /**
     * Requests multiple permissions simultaneously
     *
     * @param permissions Array of permissions to request
     * @param requestMultiplePermissions Launcher for multiple permission requests
     */
    fun requestMultiplePermissions(
        permissions: Array<String>,
        requestMultiplePermissions: ActivityResultLauncher<Array<String>>
    ) {
        Timber.d("Requesting Multiple Permissions: ${permissions.joinToString()}")
        requestMultiplePermissions.launch(permissions)
    }

    /**
     * Opens the application settings page
     * Used when permissions need to be granted manually
     *
     * @param activity Current activity context
     * @param resultLauncher Launcher for settings activity
     */
    fun openAppSettingPage(
        activity: AppCompatActivity,
        resultLauncher: ActivityResultLauncher<Intent>
    ) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        )
        Timber.d("Opening App Settings Page for Permissions")
        resultLauncher.launch(intent)
    }

    /**
     * Checks if permission rationale should be shown for a single permission
     *
     * @param activity Current activity context
     * @param permission Permission to check
     * @return true if rationale should be shown, false otherwise
     */
    private fun isShouldShowRequestPermissionRationale(
        activity: AppCompatActivity,
        permission: String
    ): Boolean {
        val shouldShowRationale = !ActivityCompat.shouldShowRequestPermissionRationale(
            activity, permission
        ) && ContextCompat.checkSelfPermission(
            activity,
            permission
        ) != PackageManager.PERMISSION_GRANTED
        Timber.v("Permission Rationale Check - $permission: $shouldShowRationale")
        return shouldShowRationale
    }

    /**
     * Checks if permission rationale should be shown for multiple permissions
     *
     * @param activity Current activity context
     * @param permissions Map of permissions to check
     * @return true if rationale should be shown for any permission, false otherwise
     */
    private fun isShouldShowRequestPermissionRationale(
        activity: AppCompatActivity,
        permissions: Map<String, Boolean>
    ): Boolean {
        permissions.entries.forEach {
            val isRationale = isShouldShowRequestPermissionRationale(activity, it.key)
            if (isRationale) {
                permissionName = it.key
                Timber.d("Permission Rationale Needed: $permissionName")
                return isRationale
            }
        }
        return false
    }
}