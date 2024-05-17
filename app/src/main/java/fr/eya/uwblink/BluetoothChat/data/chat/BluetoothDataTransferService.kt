package fr.eya.uwblink.uwbranging.BluetoothChat.data.chat

import android.bluetooth.BluetoothSocket
import android.util.Log
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.BluetoothMessage
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.chat.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket,

    ) {
    private val TAG = "BluetoothDataTransferService"
    fun listenForIncommingMessages(): Flow<BluetoothMessage> {
        return flow {
            if (!socket.isConnected) {
                Log.d(TAG, "listenForIncomingMessages: Socket is not connected")
                return@flow

            }
            val buffer = ByteArray(1024)
            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    Log.e(TAG, "Error reading incoming message", e)

                    throw TransferFailedException()
                }
                val incomingMessage = buffer.decodeToString(endIndex = byteCount)
                Log.d(TAG, "listenForIncomingMessages: Received message: $incomingMessage")
                emit(
                    buffer.decodeToString(
                        endIndex = byteCount
                    ).toBluetoothMessage(
                        isFromLocalUser = false
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes)
                Log.d(TAG, "sendMessage: Message sent successfully")

            } catch (e: IOException) {
                Log.e(TAG, "Error sending message", e)

                e.printStackTrace()
                return@withContext false
            }

            true
        }
    }
}