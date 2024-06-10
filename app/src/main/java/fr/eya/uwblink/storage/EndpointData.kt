package fr.eya.uwblink.storage
//Define a data class to hold the information we want to store.
data class EndpointData(
    val EndPointId : String ,
    val Distance : Float? ,
    val Azimuth : Float?,
    val Elevation : Float? ,
    val Timestamp : Long ,
    val ReadableDate: String = convertTimestampToReadableDate(Timestamp)

)
