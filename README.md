# Batch Code Capture

## Description
Batch Code Capture is an Android application designed to scan barcodes and QR codes using the device's camera. It leverages advanced image processing and barcode recognition technologies to deliver fast and accurate results. Key features include:

- Scanning multiple barcodes in a single session.
- Organizing scanned barcodes by session.
- Providing a user-friendly interface for efficient barcode management.

---

## Technical Features

### Technologies and Tools
- **Development Environment:** Android Studio
- **Programming Languages:** Java
- **Image Processing Framework:** CameraX
- **Barcode Recognition Library:** ML Kit Barcode Scanning

### Project Specifications
- **Application Name (Namespace):** `com.example.batchcodecapture`
- **Minimum SDK Version:** 24
- **Target SDK Version:** 34
- **Gradle Version:** 8.7
- **App Version:** 1.0
- **Permissions Required:**
  - `android.permission.CAMERA`

### Dependencies and Libraries
- **CameraX:**
  - `androidx.camera:camera-camera2:1.3.4`
  - `androidx.camera:camera-lifecycle:1.3.4`
  - `androidx.camera:camera-view:1.3.4`
- **ML Kit Barcode Scanning:**
  - `com.google.mlkit:barcode-scanning:17.3.0`

### Build Configuration
- **Java Compatibility:**
  - Source Compatibility: Java 1.8
  - Target Compatibility: Java 1.8

### Main Components
- **MainActivity:** Entry point of the application.
- **ScanningActivity:** Facilitates barcode scanning.
- **SessionActivity:** Displays scanning sessions.
- **BarcodeActivity:** Shows details of scanned barcodes.
