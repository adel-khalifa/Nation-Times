package com.unknown.nationtimes.network

sealed class NetworkState<T>(
    val bodyData: T? = null,
    val failureMessage: String? = null
) {

    // we use this class to determine our response state
    // later on we can read data by passing the body of response to base sealed class

    class OnSuccess<T>(responseBody: T) : NetworkState<T>(bodyData = responseBody)

    class OnLoading<T> : NetworkState<T>()

    class OnFailure<T>(responseFailureMessage: String) :
        NetworkState<T>(failureMessage = responseFailureMessage )
}