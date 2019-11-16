package co.droidchef.oldschool.network

interface Callback<T> {
    fun onSuccess(response: T)
    fun onFailure(networkError: NetworkError)
}