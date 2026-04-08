package com.example.tuf.core.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageHelper {
    /**
     * Copies the content of the given URI to the app's internal files directory and returns the absolute path.
     * Prevents issues with temporary URIs expiring on app restart.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        val fileName = "profile_pic_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)
        
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
