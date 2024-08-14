package fr.eya.uwblink.save

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveTextFile(context: Context, fileName: String, content: String): Boolean {
    return try {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        FileOutputStream(file).use { outputStream ->
            outputStream.write(content.toByteArray())
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}


