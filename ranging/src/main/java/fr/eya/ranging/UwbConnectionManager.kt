package fr.eya.ranging

import android.content.Context
import fr.eya.ranging.implementation.UwbConnectionManagerImpl

interface UwbConnectionManager {
    fun controllerUwbScope(endpoint: UwbEndPoint, configId: Int): UwbSessionScope

    fun controleeUwbScope(endpoint: UwbEndPoint): UwbSessionScope

    companion object {
        fun getInstance(context: Context): UwbConnectionManager {
            return UwbConnectionManagerImpl(context)
        }
    }
}