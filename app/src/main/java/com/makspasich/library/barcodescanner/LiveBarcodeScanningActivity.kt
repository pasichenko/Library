/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.makspasich.library.barcodescanner

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.makspasich.library.R
import com.makspasich.library.barcodescanner.barcodedetection.BarcodeProcessor
import com.makspasich.library.barcodescanner.camera.CameraSource
import com.makspasich.library.barcodescanner.camera.WorkflowModel
import com.makspasich.library.barcodescanner.camera.WorkflowModel.WorkflowState
import com.makspasich.library.databinding.ActivityLiveBarcodeKotlinBinding
import com.makspasich.library.databinding.TopActionBarInLiveCameraBinding
import java.io.IOException
import java.util.*

/** Demonstrates the barcode scanning workflow using camera preview.  */
class LiveBarcodeScanningActivity : AppCompatActivity(), OnClickListener {

    private lateinit var rootBinding: ActivityLiveBarcodeKotlinBinding
    private lateinit var actionBarBinding: TopActionBarInLiveCameraBinding
    private var cameraSource: CameraSource? = null
    private var promptChipAnimator: AnimatorSet? = null
    private val workflowModel: WorkflowModel by viewModels()
    private var currentWorkflowState: WorkflowState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootBinding = ActivityLiveBarcodeKotlinBinding.inflate(layoutInflater)
        actionBarBinding = rootBinding.topActionBarInLiveCamera
        setContentView(rootBinding.root)
        rootBinding.cameraPreviewGraphicOverlay.apply {
            setOnClickListener(this@LiveBarcodeScanningActivity)
            cameraSource = CameraSource(this)
        }

        promptChipAnimator =
                (AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter) as AnimatorSet).apply {
                    setTarget(rootBinding.bottomPromptChip)
                }

        actionBarBinding.closeButton.setOnClickListener(this)
        actionBarBinding.flashButton.setOnClickListener(this)

        setUpWorkflowModel()
    }

    override fun onResume() {
        super.onResume()
        if (!Utils.allPermissionsGranted(this)) {
            Utils.requestRuntimePermissions(this)
        }
        workflowModel.markCameraFrozen()
        currentWorkflowState = WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(BarcodeProcessor(rootBinding.cameraPreviewGraphicOverlay, workflowModel))
        workflowModel.setWorkflowState(WorkflowState.DETECTING)
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.close_button -> onBackPressed()
            R.id.flash_button -> {
                actionBarBinding.flashButton.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                    } else {
                        it.isSelected = true
                        cameraSource!!.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    }
                }
            }
        }
    }

    private fun startCameraPreview() {
        val cameraSource = this.cameraSource ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                rootBinding.cameraPreview.start(cameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start camera preview!", e)
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
            actionBarBinding.flashButton.isSelected = false
            rootBinding.cameraPreview.stop()
        }
    }

    private fun setUpWorkflowModel() {
        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equals(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState
            Log.d(TAG, "Current workflow state: ${currentWorkflowState!!.name}")

            val wasPromptChipGone = rootBinding.bottomPromptChip.visibility == View.GONE

            when (workflowState) {
                WorkflowState.DETECTING -> {
                    rootBinding.bottomPromptChip.visibility = View.VISIBLE
                    rootBinding.bottomPromptChip.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                WorkflowState.CONFIRMING -> {
                    rootBinding.bottomPromptChip.visibility = View.VISIBLE
                    rootBinding.bottomPromptChip.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }
                WorkflowState.SEARCHING -> {
                    rootBinding.bottomPromptChip.visibility = View.VISIBLE
                    rootBinding.bottomPromptChip.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }
                WorkflowState.FAIL -> {
                    rootBinding.bottomPromptChip.visibility = View.VISIBLE
                    rootBinding.bottomPromptChip.setText(R.string.prompt_fail)
                    stopCameraPreview()
                    Handler().postDelayed({
                        startCameraPreview()
                    }, 1000)

                }
                WorkflowState.DETECTED, WorkflowState.SEARCHED -> {
                    rootBinding.bottomPromptChip.visibility = View.GONE
                    stopCameraPreview()
                }
                else -> rootBinding.bottomPromptChip.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && rootBinding.bottomPromptChip.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        workflowModel.detectedBarcode.observe(this, Observer { barcode ->
            if (barcode != null) {
                barcode.rawValue?.let { barcodeString ->
                    if (barcodeString.contains("{dd_") and (barcodeString.substring(barcodeString.length - 1) == "}")) {
                        workflowModel.setWorkflowState(WorkflowState.CONFIRMED)
                        val intent = Intent()
                        intent.putExtra("keyProduct", barcodeString)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        workflowModel.setWorkflowState(WorkflowState.FAIL)
                    }
                }


            }
        })
    }

    companion object {
        private const val TAG = "LiveBarcodeActivity"
    }
}
