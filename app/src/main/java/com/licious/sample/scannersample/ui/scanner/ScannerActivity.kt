/**
 * ScannerActivity.kt
 * Main activity for handling the camera scanner functionality.
 * Manages camera permissions and hosts the scanner fragment.
 */
package com.licious.sample.scannersample.ui.scanner

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.licious.sample.design.ui.base.BaseActivity
import com.licious.sample.design.ui.permission.IGetPermissionListener
import com.licious.sample.design.ui.permission.PermissionUtil
import com.licious.sample.scannersample.R
import com.licious.sample.scannersample.databinding.ActivityScannerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScannerActivity : BaseActivity<ActivityScannerBinding>(), IGetPermissionListener {
    private var navController: NavController? = null

    @Inject
    lateinit var permissionUtil: PermissionUtil

    override fun getLogTag(): String = TAG

    /**
     * ActivityResultLauncher for handling single permission requests
     */
    private val requestLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        )
        { isGranted: Boolean ->
            permissionUtil.handleSinglePermissionResult(this, isGranted)
        }

    /**
     * ActivityResultLauncher for handling results from settings activity
     */
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                checkPermission()
            }
        }

    override fun getViewBinding(): ActivityScannerBinding =
        ActivityScannerBinding.inflate(layoutInflater)

    /**
     * Initializes the activity and checks for required permissions
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        checkPermission()
    }

    /**
     * Called when camera permission is granted
     * Sets up the navigation graph
     */
    override fun onPermissionGranted() {
        navController?.setGraph(R.navigation.nav_main)
    }

    /**
     * Called when permission is denied
     * Rechecks the permission status
     */
    override fun onPermissionDenied() {
        checkPermission()
    }

    /**
     * Called when permission rationale should be shown
     * Displays permission explanation dialog
     */
    override fun onPermissionRationale() {
        permissionAlertDialog()
    }

    /**
     * Initializes views and sets up navigation
     */
    private fun initView(){
        permissionUtil.setPermissionListener(this)
        navController = binding.navHostFragment.getFragment<NavHostFragment>().navController
        binding.viewToolBar.toolbar.setNavigationOnClickListener{
            finish()
        }
    }

    /**
     * Checks if camera permission is granted
     * Requests permission if not already granted
     */
    private fun checkPermission() {
        permissionUtil.apply {
            if (!hasPermission(
                    this@ScannerActivity as AppCompatActivity,
                    Manifest.permission.CAMERA
                )
            ) {
                requestPermission(Manifest.permission.CAMERA, requestLauncher)
            } else {
                navController?.setGraph(R.navigation.nav_main)
            }
        }
    }

    /**
     * Shows an alert dialog explaining why camera permission is needed
     * Provides options to open settings or deny permission
     */
    private fun permissionAlertDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.permission_required))
            setMessage(getString(R.string.permission_msg))

            setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                permissionUtil.openAppSettingPage(this@ScannerActivity as AppCompatActivity, resultLauncher)
                dialog.dismiss()
            }

            setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
                checkPermission()
            }
            show()
        }
    }

    companion object {
        private const val TAG = "ScannerActivity"
    }
}