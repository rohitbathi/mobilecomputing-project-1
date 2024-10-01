package com.example.healthapplication.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

suspend fun heartRateCalculator(uri: Uri, contentResolver: ContentResolver): Int {
    return withContext(Dispatchers.IO) {
        Log.d("HeartRateCalculator", "Starting heart rate calculation for URI: $uri")
        val result: Int
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, proj, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex ?: 0)
        Log.d("HeartRateCalculator", "Video file path: $path")
        cursor?.close()

        val retriever = MediaMetadataRetriever()
        val frameList = ArrayList<Bitmap>()
        try {
            retriever.setDataSource(path)
            val duration = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT
            )
            Log.d("HeartRateCalculator", "Video frame count: $duration")
            val frameDuration = min(duration!!.toInt(), 425)
            var i = 10
            while (i < frameDuration) {
                val bitmap = retriever.getFrameAtIndex(i)
                bitmap?.let { frameList.add(it) }
                i += 15
            }
        } catch (e: Exception) {
            Log.e("HeartRateCalculator", "Error processing video: ${e.stackTraceToString()}")
        } finally {
            retriever.release()

            var redBucket: Long
            var pixelCount: Long = 0
            val a = mutableListOf<Long>()
            for (i in frameList) {
                redBucket = 0
                for (y in 350 until 450) {
                    for (x in 350 until 450) {
                        val c: Int = i.getPixel(x, y)
                        pixelCount++
                        redBucket += Color.red(c) + Color.blue(c) + Color.green(c)
                    }
                }
                a.add(redBucket)
            }
            val b = mutableListOf<Long>()
            for (i in 0 until a.lastIndex - 5) {
                val temp = (a[i] + a[i + 1] + a[i + 2] + a[i + 3] + a[i + 4]) / 4
                b.add(temp)
            }
            var x = b[0]
            var count = 0
            for (i in 1 until b.lastIndex) {
                val p = b[i]
                if ((p - x) > 3500) {
                    count += 1
                }
                x = b[i]
            }
            val rate = ((count.toFloat()) * 60).toInt()
            result = (rate / 4)
            Log.d("HeartRateCalculator", "Heart rate result: $result BPM")
        }
        result
    }
}