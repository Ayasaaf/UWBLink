package fr.eya.ranging
/** a class that describe the uwb device */

data class UwbEndPoint(
    @JvmField val id: String, // Annotate with @JvmField for interoperability with Java
val metadata: ByteArray// Annotate with @JvmField for interoperability with Java
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UwbEndPoint) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}