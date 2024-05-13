package fr.eya.ranging.implementation

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

    ) : UwbOobEvent()
    /** an event that notifies that an uwb enpoint is most */
    data class UwbEndPointLost(override val endpoint : UwbEndPoint) : UwbOobEvent()
 /** Notifies that a message is received. */
data class MessageReceived(override val endpoint: UwbEndPoint, val message: ByteArray) :
    UwbOobEvent()
}