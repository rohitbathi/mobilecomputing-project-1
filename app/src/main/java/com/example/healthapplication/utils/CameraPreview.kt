package com.example.healthapplication.utils

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.nio.ByteBuffer
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    onFrameAnalyzed: (Bitmap?) -> Unit, // Callback when a frame is analyzed
    isMonitoring: Boolean // Pass true to turn torch on
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val executor = Executors.newSingleThreadExecutor()

    var camera: Camera? by remember { mutableStateOf(null) }

    // Image analysis use case
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetResolution(android.util.Size(640, 480)) // Adjust resolution if needed
            .build()
    }

    imageAnalysis.setAnalyzer(executor, { imageProxy ->
        if (imageProxy.format == ImageFormat.YUV_420_888) {
            val bitmap = imageProxy.toBitmap()
            onFrameAnalyzed(bitmap)
        }
        imageProxy.close() // Close the image proxy after processing
    })

    AndroidView(
        factory = { AndroidViewContext ->
            previewView.apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                try {
                    cameraProvider.unbindAll()
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis // Bind the image analysis use case
                    )
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    // Enable or disable torch based on isMonitoring state
                    camera?.cameraControl?.enableTorch(isMonitoring)

                } catch (e: Exception) {
                    Log.e("CameraPreview", "Camera binding failed: ${e.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

// Extension function to convert ImageProxy to Bitmap
private fun ImageProxy.toBitmap(): Bitmap? {
    val nv21Buffer = yuv420ToNv21(this)
    return BitmapFactory.decodeByteArray(nv21Buffer, 0, nv21Buffer.size)
}

private fun yuv420ToNv21(image: ImageProxy): ByteArray {
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}