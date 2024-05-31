

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
package fr.eya.uwblink.BluetoothChat.domain.chat

import fr.eya.uwblink.BluetoothChat.domain.BluetoothDevice
import fr.eya.uwblink.BluetoothChat.domain.BluetoothMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val isConnected :StateFlow<Boolean>
    val ScannedDevices: StateFlow<List<BluetoothDevice>>
    val PairedDevices: StateFlow<List<BluetoothDevice>>
    val errors : SharedFlow<String>
    fun startDiscovery()
    fun Stop()
    fun StartBluetoothServer(): Flow<ConnectionResult>
    fun ConnectToDevice(device: BluetoothDevice): Flow<ConnectionResult>
    suspend fun trySendMessage(message: String): BluetoothMessage?
    fun CloseConnection()
    fun Release()
}