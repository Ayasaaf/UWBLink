package fr.eya.uwblink.storage

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// to write the endpoint data in a .json file
fun writeDataToFile(context: Context, data: String, fileName: String) {
    val file = File(context.filesDir, fileName)
    FileOutputStream(file).use { output ->
        output.write(data.toByteArray())
    }
}
//convertir le temps en format lisible
fun convertTimestampToReadableDate(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

fun readDataFromFile(context: Context, fileName: String): List<EndpointData>? {
    return try {
        val fileInputStream = context.openFileInput(fileName)
        val size = fileInputStream.available()
        val buffer = ByteArray(size)
        fileInputStream.read(buffer)
        fileInputStream.close()

        val json = String(buffer, Charsets.UTF_8)
        val gson = Gson()
        val listType = object : TypeToken<List<EndpointData>>() {}.type
        val dataList = gson.fromJson<List<EndpointData>>(json, listType)

        // Add readable date to each data entry
        dataList.map { it.copy(ReadableDate = convertTimestampToReadableDate(it.Timestamp)) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun printReadableData(context: Context, fileName: String) {
    val data = readDataFromFile(context, fileName)
    data?.forEach { endpoint ->
        println("EndPointId: ${endpoint.EndPointId}")
        println("Distance: ${endpoint.Distance}")
        println("Azimuth: ${endpoint.Azimuth}")
        println("Elevation: ${endpoint.Elevation}")
        println("Timestamp: ${convertTimestampToReadableDate(endpoint.Timestamp)}")
        println("ReadableDate: ${endpoint.ReadableDate}")
    }
}
fun saveJsonAsTextFile(context: Context, fileName: String, jsonContent: String): Boolean {
    return try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        FileOutputStream(file).use { outputStream ->
            outputStream.write(jsonContent.toByteArray())
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}