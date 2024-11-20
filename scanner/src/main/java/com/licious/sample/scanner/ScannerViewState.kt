/**
 * ScannerViewState.kt
 * Sealed class representing possible states of the scanner operation
 * Success: Indicates successful barcode/QR code detection and decoding
 * Error: Indicates failure in scanning process
 */
package com.licious.sample.scanner

sealed class ScannerViewState {
    object Success : ScannerViewState()
    object Error : ScannerViewState()
}