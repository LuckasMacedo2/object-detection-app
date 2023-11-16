package com.lucas.myapp.Utils

import android.content.ContentResolver
import android.net.Uri
import java.io.*

class ArquivosUtil {
    companion object {
        fun convertUriToFile(uri: Uri, contentResolver: ContentResolver, cacheDir: File): File? {
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val file = File(cacheDir, "temp_image.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                return file
            } catch (e: Exception) {
                return null
            }
        }
    }


}