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

import android.graphics.RectF
import com.google.android.gms.common.images.Size
import com.google.mlkit.vision.barcode.Barcode
import com.makspasich.library.barcodescanner.camera.CameraSizePair
import com.makspasich.library.barcodescanner.camera.GraphicOverlay

/** Utility class to retrieve shared preferences.  */
object SettingUtils {
    /**
     * Relative to the camera view width, ranges from 50% to 95%
     */
    private const val BARCODE_RETICLE_WIDTH = 80

    /**
     *Relative to the camera view height, ranges from 20% to 80%
     */
    private const val BARCODE_RETICLE_HEIGHT = 35

    /**
     * Relative to the reticle width, ranges from 20% to 80% (only applicable when barcode size check enabled)
     */
    private const val MINIMUM_BARCODE_WIDTH = 20

    /**
     * Will show the loading spinner for 2s
     */
    private const val DELAY_LOADING_BARCODE_RESULT = false
    private const val BARCODE_SIZE_CHECK = true
    private const val PREVIEW_SIZE = "864x480"
    private const val PICTURE_SIZE = "864x480"


    fun getProgressToMeetBarcodeSizeRequirement(
            overlay: GraphicOverlay,
            barcode: Barcode
    ): Float {
        return if (BARCODE_SIZE_CHECK) {
            val reticleBoxWidth = getBarcodeReticleBox(overlay).width()
            val barcodeWidth = overlay.translateX(barcode.boundingBox?.width()?.toFloat() ?: 0f)
            val requiredWidth = reticleBoxWidth * MINIMUM_BARCODE_WIDTH / 100
            (barcodeWidth / requiredWidth).coerceAtMost(1f)
        } else {
            1f
        }
    }

    fun getBarcodeReticleBox(overlay: GraphicOverlay): RectF {
        val overlayWidth = overlay.width.toFloat()
        val overlayHeight = overlay.height.toFloat()
        val boxWidth = overlayWidth * BARCODE_RETICLE_WIDTH / 100
        val boxHeight = overlayHeight * BARCODE_RETICLE_HEIGHT / 100
        val cx = overlayWidth / 2
        val cy = overlayHeight / 2
        return RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2)
    }

    fun shouldDelayLoadingBarcodeResult(): Boolean = DELAY_LOADING_BARCODE_RESULT

    fun getUserSpecifiedPreviewSize(): CameraSizePair? {
        return try {
            CameraSizePair(
                    Size.parseSize(PREVIEW_SIZE),
                    Size.parseSize(PICTURE_SIZE)
            )
        } catch (e: Exception) {
            null
        }
    }
}
