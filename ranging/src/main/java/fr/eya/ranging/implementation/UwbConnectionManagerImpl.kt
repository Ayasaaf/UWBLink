package fr.eya.ranging.implementation

import android.content.Context
import androidx.core.uwb.UwbManager
import fr.eya.ranging.UwbConnectionManager
import fr.eya.ranging.UwbEndPoint
import fr.eya.ranging.UwbSessionScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class UwbConnectionManagerImpl(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UwbConnectionManager {

    private val uwbManager = UwbManager.createInstance(context)

    override fun controllerUwbScope(endpoint: UwbEndPoint, configId: Int): UwbSessionScope {
        val connector =
            NearByControllerConnector(endpoint, configId, NearByConnection(context, dispatcher)) {
                uwbManager.controllerSessionScope()
            }
        return UwbSessionScopeImpl(endpoint, connector)
    }

    override fun controleeUwbScope(endpoint: UwbEndPoint): UwbSessionScope {
        val connector =
            NearByControleeConnector(endpoint, NearByConnection(context, dispatcher)) {
                uwbManager.controleeSessionScope()
            }
        return UwbSessionScopeImpl(endpoint, connector)
    }
}