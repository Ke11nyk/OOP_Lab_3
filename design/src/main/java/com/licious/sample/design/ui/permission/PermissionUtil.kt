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
        when {
            isGranted -> {
                permissionListener?.onPermissionGranted()
            }
            isShouldShowRequestPermissionRationale(activity, permissionName) -> {
                permissionListener?.onPermissionRationale()
            }
            else -> {
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
        var isGranted = true
        permissions.entries.forEach {
            if (!it.value) {
                isGranted = false
                return@forEach
            }
        }
        when {
            isGranted -> {
                permissionListener?.onPermissionGranted()
            }
            isShouldShowRequestPermissionRationale(activity, permissions) -> {
                permissionListener?.onPermissionRationale()
            }
            else -> {
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
        return ContextCompat.checkSelfPermission(
            context,
            permissionName
        ) == PackageManager.PERMISSION_GRANTED
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
                return false
            }
        }
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
        return !ActivityCompat.shouldShowRequestPermissionRationale(
            activity, permission
        ) && ContextCompat.checkSelfPermission(
            activity,
            permission
        ) != PackageManager.PERMISSION_GRANTED
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
                return isRationale
            }
        }
        return false
    }
}