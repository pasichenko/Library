package com.makspasich.library

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.makspasich.library.databinding.BarcodeScannerActivityBinding
import java.io.IOException

class BarcodeScannerActivity : AppCompatActivity() {
    private lateinit var binding: BarcodeScannerActivityBinding
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    //This class provides methods to play DTMF tones
    private var toneGen1: ToneGenerator? = null
    private lateinit var barcodeData: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BarcodeScannerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        initialiseDetectorsAndSources()
    }

    private fun initialiseDetectorsAndSources() {
        barcodeDetector = BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(3000, 4000)
                .setAutoFocusEnabled(true) //you should add this feature
                .build()
        binding.surfaceView.holder.addCallback(surfaceHolderCallback)
        barcodeDetector.setProcessor(barcodeProcessor)
    }

    private val surfaceHolderCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                if (ActivityCompat.checkSelfPermission(this@BarcodeScannerActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(binding.surfaceView.holder)
                } else {
                    ActivityCompat.requestPermissions(this@BarcodeScannerActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }
    }
    private val barcodeProcessor: Detector.Processor<Barcode> = object : Detector.Processor<Barcode> {
        override fun release() {
            // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
        }

        override fun receiveDetections(detections: Detections<Barcode>) {
            val barcodes = detections.detectedItems
            if (barcodes.size() != 0) {
                binding.barcodeText.post(Runnable {
                    if (barcodes.valueAt(0).email != null) {
                        binding.barcodeText.removeCallbacks(null)
                        barcodeData = barcodes.valueAt(0).email.address
                        binding.barcodeText.text = barcodeData
                        toneGen1!!.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                    } else {
                        barcodeData = barcodes.valueAt(0).displayValue
                        if (barcodeData.contains("{dd_") and (barcodeData.substring(barcodeData.length - 1) == "}")) {
                            binding.barcodeText.text = barcodeData
                            val intent = Intent()
                            intent.putExtra("keyProduct", barcodeData)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            binding.barcodeText.text = "FAIL CODE"
                        }
                    }
                })
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 201
    }
}