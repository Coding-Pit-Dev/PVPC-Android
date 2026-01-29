package com.codingpit.pvpcplanner.domain.error

import android.content.Context
import com.codingpit.pvpcplanner.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for providing standardized error messages throughout the application.
 * Enables consistent error messaging and easy localization.
 */
interface ErrorMessageProvider {
    /**
     * Gets a user-friendly error message for network-related errors.
     */
    fun getNetworkErrorMessage(errorCode: Int? = null): String

    /**
     * Gets a user-friendly error message for data-related errors.
     */
    fun getDataErrorMessage(context: String? = null): String

    /**
     * Gets a user-friendly error message for validation errors.
     */
    fun getValidationErrorMessage(field: String? = null): String

    /**
     * Gets a user-friendly error message for generic errors.
     */
    fun getGenericErrorMessage(): String

    /**
     * Gets a user-friendly error message for timeout errors.
     */
    fun getTimeoutErrorMessage(): String

    /**
     * Gets a user-friendly error message for authentication errors.
     */
    fun getAuthenticationErrorMessage(): String
}

/**
 * Default implementation of ErrorMessageProvider with localized messages.
 * This implementation provides consistent, user-friendly error messages.
 */
@Singleton
class DefaultErrorMessageProvider
@Inject
constructor(
    @ApplicationContext private val context: Context,
) : ErrorMessageProvider {
    override fun getNetworkErrorMessage(errorCode: Int?): String =
        when (errorCode) {
            400 -> context.getString(R.string.error_network_400)
            401 -> context.getString(R.string.error_network_401)
            403 -> context.getString(R.string.error_network_403)
            404 -> context.getString(R.string.error_network_404)
            429 -> context.getString(R.string.error_network_429)
            500, 502, 503 -> context.getString(R.string.error_network_server)
            else -> context.getString(R.string.error_network_generic)
        }

    override fun getDataErrorMessage(context: String?): String =
        when (context) {
            "price_data" -> this.context.getString(R.string.error_data_price)
            "device_data" -> this.context.getString(R.string.error_data_device)
            "current_hour" -> this.context.getString(R.string.error_data_current_hour)
            "settings_data" -> this.context.getString(R.string.error_data_settings)
            else -> this.context.getString(R.string.error_data_generic)
        }

    override fun getValidationErrorMessage(field: String?): String =
        when (field) {
            "date" -> context.getString(R.string.error_validation_date)
            "device_name" -> context.getString(R.string.error_validation_device_name)
            "device_hours" -> context.getString(R.string.error_validation_device_hours)
            "price" -> context.getString(R.string.error_validation_price)
            else -> context.getString(R.string.error_validation_generic)
        }

    override fun getGenericErrorMessage(): String = context.getString(R.string.error_generic)

    override fun getTimeoutErrorMessage(): String = context.getString(R.string.error_timeout)

    override fun getAuthenticationErrorMessage(): String = context.getString(R.string.error_auth)
}

/**
 * Extension functions to make error message retrieval more convenient.
 */
object ErrorMessages {
    /**
     * Common error message categories for easy access.
     */
    enum class Category {
        NETWORK,
        DATA,
        VALIDATION,
        TIMEOUT,
        AUTHENTICATION,
        GENERIC,
    }

    /**
     * Gets an error message for a specific category with optional context.
     */
    fun ErrorMessageProvider.getMessage(
        category: Category,
        context: String? = null,
        errorCode: Int? = null,
    ): String =
        when (category) {
            Category.NETWORK -> getNetworkErrorMessage(errorCode)
            Category.DATA -> getDataErrorMessage(context)
            Category.VALIDATION -> getValidationErrorMessage(context)
            Category.TIMEOUT -> getTimeoutErrorMessage()
            Category.AUTHENTICATION -> getAuthenticationErrorMessage()
            Category.GENERIC -> getGenericErrorMessage()
        }
}
