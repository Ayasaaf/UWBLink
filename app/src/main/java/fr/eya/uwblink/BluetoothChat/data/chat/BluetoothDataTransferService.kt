
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

import android.bluetooth.BluetoothSocket
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.BluetoothMessage
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.chat.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket

) { fun listenForIncommingMessages():Flow<BluetoothMessage>{
return  flow {
    if(!socket.isConnected) {
        return@flow
    }
    val buffer = ByteArray(1024)
    while(true) {
        val byteCount = try {
            socket.inputStream.read(buffer)
        } catch(e: IOException) {
            throw TransferFailedException()
        }

        emit (
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
        } catch(e: IOException) {
            e.printStackTrace()
            return@withContext false
        }

        true
    }
}
}