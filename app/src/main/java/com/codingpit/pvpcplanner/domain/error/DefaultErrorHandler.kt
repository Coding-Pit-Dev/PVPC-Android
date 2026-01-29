package com.codingpit.pvpcplanner.domain.error

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DefaultErrorHandler @Inject constructor(
    private val errorMessageProvider: ErrorMessageProvider,
) : ErrorHandler {
    override fun handleError(throwable: Throwable): ErrorResult = handleError(throwable, null)

    override fun handleError(
        throwable: Throwable,
        context: String?,
    ): ErrorResult {
        if (throwable is CancellationException) {
            throw throwable
        }

        return when (throwable) {
            is HttpException -> handleHttpException(throwable, context)
            is IOException -> handleNetworkException(throwable, context)
            is NoSuchElementException -> handleDataException(throwable, context)
            is IllegalArgumentException -> handleValidationException(throwable, context)
            else ->
                ErrorResult.UnknownError(
                    message = errorMessageProvider.getGenericErrorMessage(),
                    context = context,
                )
        }
    }

    private fun handleHttpException(
        exception: HttpException,
        context: String?,
    ): ErrorResult {
        val message = errorMessageProvider.getNetworkErrorMessage(exception.code())
        return ErrorResult.NetworkError(message, context)
    }

    private fun handleNetworkException(
        exception: IOException,
        context: String?,
    ): ErrorResult {
        val message =
            when {
                exception is java.net.SocketTimeoutException ||
                        exception is java.net.ConnectException ||
                        exception.cause is java.net.SocketTimeoutException ->
                    errorMessageProvider.getTimeoutErrorMessage()

                else -> errorMessageProvider.getNetworkErrorMessage()
            }

        return ErrorResult.NetworkError(message, context)
    }

    private fun handleDataException(
        exception: NoSuchElementException,
        context: String?,
    ): ErrorResult {
        val message = errorMessageProvider.getDataErrorMessage(context)
        return ErrorResult.DataError(message, context)
    }

    private fun handleValidationException(
        exception: IllegalArgumentException,
        context: String?,
    ): ErrorResult =
        ErrorResult.ValidationError(
            message = errorMessageProvider.getValidationErrorMessage(context),
            context = context,
        )
}
