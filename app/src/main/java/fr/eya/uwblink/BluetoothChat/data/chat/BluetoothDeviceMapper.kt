
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
package fr.eya.uwblink.BluetoothChat.data.chat



import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import fr.eya.uwblink.BluetoothChat.domain.BluetoothDeviceDomain


@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
   return BluetoothDeviceDomain(
       name = name ,
       address = address ,


    )
} // la fonction tobluetoothdevice domain perment d encapsuler notre bluetooth device dans un bluetooth device domain
// le but est de creer un bluetooth device avec les informations pertinantes qu'on a besoin : nom et adresse