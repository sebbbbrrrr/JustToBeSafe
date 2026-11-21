package com.justtobesafe.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.common.InputImage
import com.justtobesafe.app.domain.validFoodBarcode
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/** Bundled ML Kit model: detection works offline and without Play Services. */
@Composable
fun BarcodeScannerDialog(onDetected: (String) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var permitted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED)
    }
    var cameraError by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> permitted = granted }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surface
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Scan a food barcode", style = MaterialTheme.typography.titleLarge)
                Text("Hold an EAN or UPC barcode in view. Scanning works offline; identifying the product needs internet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (permitted) {
                    Box(Modifier.fillMaxWidth().height(270.dp), contentAlignment = Alignment.Center) {
                        ScannerCamera(
                            modifier = Modifier.fillMaxWidth().height(270.dp),
                            onDetected = onDetected,
                            onError = { cameraError = it }
                        )
                    }
                    if (cameraError != null) Text(cameraError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                } else {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Column(Modifier.fillMaxWidth().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Camera access is needed only while scanning. You can also add items by hand.")
                            Button(onClick = {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }) { Text("Allow camera") }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Close") }
                }
            }
        }
    }
}

@Composable
private fun ScannerCamera(
    modifier: Modifier,
    onDetected: (String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    AndroidView(factory = { previewView }, modifier = modifier)

    DisposableEffect(lifecycleOwner, previewView) {
        val active = AtomicBoolean(true)
        val processing = AtomicBoolean(false)
        val emitted = AtomicBoolean(false)
        val executor = Executors.newSingleThreadExecutor()
        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_ITF
            ).build()
        )
        var cameraProvider: ProcessCameraProvider? = null
        var preview: Preview? = null
        var analysis: ImageAnalysis? = null
        val mainExecutor = ContextCompat.getMainExecutor(context)
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            if (!active.get()) return@addListener
            try {
                val provider = future.get()
                val previewUseCase = Preview.Builder().build().apply {
                    surfaceProvider = previewView.surfaceProvider
                }
                val analysisUseCase = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analysisUseCase.setAnalyzer(executor) { proxy ->
                    if (!active.get() || emitted.get() || !processing.compareAndSet(false, true)) {
                        proxy.close()
                        return@setAnalyzer
                    }
                    val image = proxy.image
                    if (image == null) {
                        processing.set(false)
                        proxy.close()
                        return@setAnalyzer
                    }
                    try {
                        scanner.process(InputImage.fromMediaImage(image, proxy.imageInfo.rotationDegrees))
                            .addOnSuccessListener(mainExecutor) { barcodes ->
                                val code = barcodes.mapNotNull { it.rawValue?.trim() }
                                    .firstOrNull { it.isNotEmpty() && validFoodBarcode(it) }
                                if (active.get() && code != null && emitted.compareAndSet(false, true)) {
                                    onDetected(code)
                                }
                            }
                            .addOnCompleteListener(mainExecutor) {
                                processing.set(false)
                                proxy.close()
                            }
                    } catch (_: Exception) {
                        processing.set(false)
                        proxy.close()
                    }
                }
                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA,
                    previewUseCase, analysisUseCase)
                cameraProvider = provider
                preview = previewUseCase
                analysis = analysisUseCase
            } catch (_: Exception) {
                if (active.get()) onError("Camera unavailable. Add the item manually instead.")
            }
        }, mainExecutor)

        onDispose {
            active.set(false)
            analysis?.clearAnalyzer()
            // Unbind only our use cases, never other cameras.
            preview?.let { pv -> analysis?.let { an -> cameraProvider?.unbind(pv, an) } }
            scanner.close()
            executor.shutdown()
        }
    }
}
