package fr.eya.uwblink.Save

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
fun readTextFile(context: Context, fileName: String): String? {
    return try {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (file.exists()) {
            file.readText(Charsets.UTF_8)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
