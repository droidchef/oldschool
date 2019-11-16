package co.droidchef.oldschool.network

sealed class NetworkError {

    data class YourPhoneScrewedUp(val message: String) : NetworkError()

    data class OurClientScrewedUp(val message: String) : NetworkError()

    data class OurServerScrewedUp(val message: String) : NetworkError()

}