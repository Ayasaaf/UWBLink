package fr.eya.ranging.implementation

import android.util.Log
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbClientSessionScope
import androidx.core.uwb.UwbComplexChannel
import fr.eya.ranging.UwbEndPoint

internal abstract class UwbOobEvent  private constructor(){
    abstract val endpoint:UwbEndPoint
    /** An event that notifies an endpoint is found through OOB */
    data class UwbEndPointFound (
        override val endpoint : UwbEndPoint ,
        val configId : Int ,
        val endpointAddress : UwbAddress ,
        val complexChannel: UwbComplexChannel ,
        val sessionId: Int ,
        val sessionKeyInfo : ByteArray ,
        val sessionScope: UwbClientSessionScope ,

    ) : UwbOobEvent() {
        init {
            Log.d("SEE", "UwbEndPointFound created: $this")
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UwbEndPointFound

            if (endpoint != other.endpoint) return false
            if (configId != other.configId) return false
            if (endpointAddress != other.endpointAddress) return false
            if (complexChannel != other.complexChannel) return false
            if (sessionId != other.sessionId) return false
            if (!sessionKeyInfo.contentEquals(other.sessionKeyInfo)) return false
            return sessionScope == other.sessionScope
        }

        override fun hashCode(): Int {
            var result = endpoint.hashCode()
            result = 31 * result + configId
            result = 31 * result + endpointAddress.hashCode()
            result = 31 * result + complexChannel.hashCode()
            result = 31 * result + sessionId
            result = 31 * result + sessionKeyInfo.contentHashCode()
            result = 31 * result + sessionScope.hashCode()
            return result
        }
    }

    /** an event that notifies that an uwb enpoint is most */
    data class UwbEndPointLost(override val endpoint : UwbEndPoint) : UwbOobEvent()
 /** Notifies that a message is received. */
data class MessageReceived(override val endpoint: UwbEndPoint, val message: ByteArray) :
    UwbOobEvent()
}