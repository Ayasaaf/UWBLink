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

package fr.eya.uwblink.uwbranging.BluetoothChat.data.chat

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import fr.eya.uwblink.R
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.BluetoothDeviceDomain
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.BluetoothMessage
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.chat.BluetoothController
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.chat.ConnectionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID


@SuppressLint("MissingPermission")

class AndroidBluetoothController(
    private val context: Context,

    ) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private var dataTransferService: BluetoothDataTransferService? = null
    private val _isConnected = MutableStateFlow(false)


    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val ScannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val PairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String
            >()
    override val errors: SharedFlow<String>
        get() = _errors

    //update the list of devices when a new device is scanned
    private val FoundDeviceReciever = FoundDeviceReciever { device ->
        _scannedDevices.update { devices ->
            val NewDevice = device.toBluetoothDeviceDomain()
            if (NewDevice in devices) devices else devices + NewDevice
        }
    }

    // update the state to is connected for a device scanned when it is paired
    private val BluetoothStateReciever = BluetoothStateReciever { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }

        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("can't connect to a non-paired device ")
                //   PairingNotification(context = context , Device.address , Device.name)
            }

        }
    }
    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    init {
        UpdateDevices()
        context.registerReceiver(
            BluetoothStateReciever,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )


    }

    override fun startDiscovery() {
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        context.registerReceiver(
            FoundDeviceReciever,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )



        UpdateDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun Stop() {
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun StartBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("NO_BLUETOOTH_CONNECT_PERMISSION")
            }

            currentServerSocket = bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(
                "CHAT_SERVICE", UUID.fromString(SERVICE_UUID)
            )
            var ShouldLoop = true
            while (ShouldLoop) {
                currentClientSocket = try {

                    currentServerSocket?.accept()
                } catch (_: IOException) {
                    ShouldLoop = false
                    null

                }
                emit(ConnectionResult.ConnectionEstablished)
                currentClientSocket?.let {
                    currentServerSocket?.close()
                    val service = BluetoothDataTransferService(it)
                    dataTransferService = service
                    emitAll(
                        service
                            .listenForIncommingMessages()
                            .map {
                                ConnectionResult.transfersucceded(it)
                            }
                    )
                }

            }
        }.onCompletion {
            CloseConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun ConnectToDevice(Device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("NO_BLUETOOTH_CONNECT_PERMISSION")
            }

            currentClientSocket = bluetoothAdapter?.getRemoteDevice(Device.address)
                ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))
            Stop()


            currentClientSocket?.let { socket ->
                try {
                    PairingNotification(
                        context = context,
                        Device = BluetoothDeviceDomain(Device.address, Device.name)
                    )
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished)
                    BluetoothDataTransferService(socket).also {
                        dataTransferService = it
                        emitAll(
                            it.listenForIncommingMessages()
                                .map {
                                    ConnectionResult.transfersucceded(it)
                                }
                        )
                    }

                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error("Connection was Interrupted "))
                }
            }
        }.onCompletion {
            CloseConnection()
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun trySendMessage(message: String): BluetoothMessage? {
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)) {
            return null
        }

        if (dataTransferService == null) {
            return null
        }

        val bluetoothMessage = BluetoothMessage(
            message = message,
            senderName = bluetoothAdapter?.name ?: "Unknown name",
            isFromLocalUser = true
        )

        dataTransferService?.sendMessage(bluetoothMessage.toByteArray())

        return bluetoothMessage
    }

    override fun CloseConnection() {
        currentServerSocket?.close()
        currentServerSocket?.close()
        currentServerSocket = null
        currentClientSocket = null
    }

    override fun Release() {
        context.unregisterReceiver(FoundDeviceReciever)
        context.unregisterReceiver(BluetoothStateReciever)
        CloseConnection()
    }

    @SuppressLint("MissingPermission")
    private fun UpdateDevices() {
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
        ) {
            return
        }
        bluetoothAdapter?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val SERVICE_UUID = "4df18ff6-d922-4bde-ac72-6b25fc7b73ab"
    }


    fun PairingNotification(context: Context, Device: BluetoothDeviceDomain) {
        val permission = android.Manifest.permission.BLUETOOTH_ADMIN

        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val CHANNEL_ID = "my_channel_id "
            val NotificatioId = 1
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("PairingRequest")
                .setContentText("The device ${Device.name} want to pair you .").setSmallIcon(
                    R.drawable.ic_notification
                ).addAction(R.drawable.ic_accept, "accept", acceptPendingIntent(context, Device))
                .addAction(R.drawable.ic_decline, "Refuse", declinePendingIntent(context, Device))
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NotificatioId, notificationBuilder.build())
        }

    }


    private fun acceptPendingIntent(
        context: Context,
        Device: BluetoothDeviceDomain
    ): PendingIntent {
        val permission = android.Manifest.permission.BLUETOOTH_ADMIN
        val intent =
            Intent(context, fr.eya.uwblink.uwbranging.BluetoothChat.data.chat.FoundDeviceReciever::class.java)
        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED
        )

            intent.action = "com.example.bluetooth.PAIRING_ACCEPTED"
        intent.putExtra("Device Name", Device.name)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)


    }


    private fun declinePendingIntent(
        context: Context,
        Device: BluetoothDeviceDomain
    ): PendingIntent {
        val permission = android.Manifest.permission.BLUETOOTH_ADMIN
        val intent = Intent(context, BroadcastReceiver::class.java)
        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED
        )

            intent.action = "com.example.bluetooth.PAIRING_DECLINED"
        intent.putExtra("Device Name", Device.name)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    }
}




