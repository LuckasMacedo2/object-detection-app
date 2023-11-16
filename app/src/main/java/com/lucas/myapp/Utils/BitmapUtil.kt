package com.lucas.myapp.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class BitmapUtil {
    companion object {
        fun bitmapToUri(context: Context, resizedBitmap: Bitmap): Uri? {
            try {
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image_${System.currentTimeMillis()}.png")

                FileOutputStream(file).use { fos -> resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos) }

                return Uri.fromFile(file)

            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }

        fun saveBitmapAsUri(context: Context, bitmap: Bitmap, quality: Int = 100, maxWidth: Int = 1024, maxHeight: Int = 1024): Uri? {
            var imageUri: Uri? = null
            try {
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()

                val uniqueFileName = "image_${System.currentTimeMillis()}.png"


                val scaledBitmap = scaleBitmap(bitmap, maxWidth, maxHeight)

                val stream: OutputStream = FileOutputStream(cachePath.resolve(uniqueFileName))
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
                stream.close()
                imageUri = Uri.fromFile(cachePath.resolve(uniqueFileName))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return imageUri
        }

        fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height

            val scaleWidth = maxWidth.toFloat() / width
            val scaleHeight = maxHeight.toFloat() / height
            val scale = minOf(scaleWidth, scaleHeight)

            val matrix = Matrix()
            matrix.postScale(scale, scale)

            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        }
    }
}