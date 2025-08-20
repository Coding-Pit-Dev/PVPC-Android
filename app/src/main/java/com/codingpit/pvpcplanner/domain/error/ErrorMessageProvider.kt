package com.codingpit.pvpcplanner.domain.error

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
 * Default implementation of ErrorMessageProvider with Spanish-friendly messages.
 * This implementation provides consistent, user-friendly error messages.
 */
@Singleton
class DefaultErrorMessageProvider @Inject constructor() : ErrorMessageProvider {
    
    override fun getNetworkErrorMessage(errorCode: Int?): String {
        return when (errorCode) {
            400 -> "Solicitud incorrecta. Por favor, verifica los datos introducidos."
            401 -> "No tienes permisos para acceder a esta información."
            403 -> "Acceso denegado a este recurso."
            404 -> "La información solicitada no está disponible."
            429 -> "Demasiadas solicitudes. Por favor, inténtalo de nuevo más tarde."
            500, 502, 503 -> "Error del servidor. Por favor, inténtalo de nuevo más tarde."
            else -> "Error de conexión. Por favor, verifica tu conexión a Internet."
        }
    }
    
    override fun getDataErrorMessage(context: String?): String {
        return when (context) {
            "price_data" -> "No hay datos de precios disponibles para la fecha seleccionada."
            "device_data" -> "No se encontró información del dispositivo."
            "current_hour" -> "No hay datos de precio disponibles para la hora actual."
            "settings_data" -> "Error al cargar la configuración de la aplicación."
            else -> "Los datos solicitados no están disponibles en este momento."
        }
    }
    
    override fun getValidationErrorMessage(field: String?): String {
        return when (field) {
            "date" -> "La fecha introducida no es válida."
            "device_name" -> "El nombre del dispositivo no es válido."
            "device_hours" -> "El número de horas debe ser mayor que 0."
            "price" -> "El valor del precio no es válido."
            else -> "Los datos introducidos no son válidos."
        }
    }
    
    override fun getGenericErrorMessage(): String {
        return "Ha ocurrido un error inesperado. Por favor, inténtalo de nuevo."
    }
    
    override fun getTimeoutErrorMessage(): String {
        return "La operación ha tardado demasiado tiempo. Por favor, verifica tu conexión a Internet."
    }
    
    override fun getAuthenticationErrorMessage(): String {
        return "Tu sesión ha expirado. Por favor, vuelve a iniciar sesión."
    }
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
        GENERIC
    }
    
    /**
     * Gets an error message for a specific category with optional context.
     */
    fun ErrorMessageProvider.getMessage(
        category: Category,
        context: String? = null,
        errorCode: Int? = null
    ): String {
        return when (category) {
            Category.NETWORK -> getNetworkErrorMessage(errorCode)
            Category.DATA -> getDataErrorMessage(context)
            Category.VALIDATION -> getValidationErrorMessage(context)
            Category.TIMEOUT -> getTimeoutErrorMessage()
            Category.AUTHENTICATION -> getAuthenticationErrorMessage()
            Category.GENERIC -> getGenericErrorMessage()
        }
    }
}