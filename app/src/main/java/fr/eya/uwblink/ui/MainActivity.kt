/*
 *
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */



package fr.eya.uwblink.ui

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import fr.eya.uwblink.HelloUwbApplication
import fr.eya.uwblink.R


private const val PERMISSION_REQUEST_CODE = 1234


@AndroidEntryPoint

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()

        createNotificationChannel()
        (application as HelloUwbApplication).initContainer {
            runOnUiThread {
                setContent {
                    HelloUwbApp(
                        (application as HelloUwbApplication).container
                    )
                }
            }
        }

        /**
         * Check if device supports Ultra-wideband
         */
        val packageManager: PackageManager = applicationContext.packageManager
        val deviceSupportsUwb = packageManager.hasSystemFeature("android.hardware.uwb")

        if (!deviceSupportsUwb) {
            Log.e("UWB Sample", "Device does not support Ultra-wideband")
            Toast.makeText(applicationContext, "Device does not support UWB", Toast.LENGTH_SHORT)
                .show()
            //TODO: Uncomment this if you want to see it running on a non-supported device
            finishAndRemoveTask()
        } else {
            Toast.makeText(applicationContext, "Device supports UWB", Toast.LENGTH_SHORT).show()
        }


    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        if (!arePermissionsGranted()) {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSION_REQUEST_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun arePermissionsGranted(): Boolean {
        for (permission in PERMISSIONS_REQUIRED) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arePermissionsGranted()
                } else {
                    TODO("VERSION.SDK_INT < TIRAMISU")
                }
            ) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val PERMISSIONS_REQUIRED_BEFORE_T =
            listOf(
                // Permissions needed by Nearby Connection
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,

                // permission required by UWB API
                Manifest.permission.UWB_RANGING,
                //permission for notification
                Manifest.permission.POST_NOTIFICATIONS
            )
        const val CHANNEL_ID = "IMAGE_RECEIVED_CHANNEL"

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val PERMISSIONS_REQUIRED_T =
            arrayOf(
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.POST_NOTIFICATIONS
            )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val PERMISSIONS_REQUIRED =
            PERMISSIONS_REQUIRED_BEFORE_T.toMutableList()
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        addAll(PERMISSIONS_REQUIRED_T)
                    }
                }
                .toTypedArray()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}