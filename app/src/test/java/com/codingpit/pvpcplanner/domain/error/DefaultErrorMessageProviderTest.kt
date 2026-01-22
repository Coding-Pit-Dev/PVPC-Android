package com.codingpit.pvpcplanner.domain.error

import android.content.Context
import com.codingpit.pvpcplanner.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultErrorMessageProviderTest {

    private lateinit var errorMessageProvider: DefaultErrorMessageProvider
    private val context: Context = mockk()

    @Before
    fun setup() {
        errorMessageProvider = DefaultErrorMessageProvider(context)
        every { context.getString(R.string.error_network_404) } returns "La información solicitada no está disponible."
        every { context.getString(R.string.error_network_server) } returns "Error del servidor. Por favor, inténtalo de nuevo más tarde."
        every { context.getString(R.string.error_network_generic) } returns "Error de conexión. Por favor, verifica tu conexión a Internet."
        every { context.getString(R.string.error_data_price) } returns "No hay datos de precios disponibles para la fecha seleccionada."
        every { context.getString(R.string.error_data_device) } returns "No se encontró información del dispositivo."
        every { context.getString(R.string.error_data_generic) } returns "Los datos solicitados no están disponibles en este momento."
        every { context.getString(R.string.error_validation_date) } returns "La fecha introducida no es válida."
        every { context.getString(R.string.error_validation_device_name) } returns "El nombre del dispositivo no es válido."
        every { context.getString(R.string.error_validation_generic) } returns "Los datos introducidos no son válidos."
        every { context.getString(R.string.error_generic) } returns "Ha ocurrido un error inesperado. Por favor, inténtalo de nuevo."
        every { context.getString(R.string.error_timeout) } returns "La operación ha tardado demasiado tiempo. Por favor, verifica tu conexión a Internet."
        every { context.getString(R.string.error_auth) } returns "Tu sesión ha expirado. Por favor, vuelve a iniciar sesión."
    }

    @Test
    fun `getNetworkErrorMessage returns specific message for 404`() {
        // Act
        val result = errorMessageProvider.getNetworkErrorMessage(404)

        // Assert
        assertEquals("La información solicitada no está disponible.", result)
    }

    @Test
    fun `getNetworkErrorMessage returns specific message for 500`() {
        // Act
        val result = errorMessageProvider.getNetworkErrorMessage(500)

        // Assert
        assertEquals("Error del servidor. Por favor, inténtalo de nuevo más tarde.", result)
    }

    @Test
    fun `getNetworkErrorMessage returns generic message for unknown code`() {
        // Act
        val result = errorMessageProvider.getNetworkErrorMessage(999)

        // Assert
        assertEquals("Error de conexión. Por favor, verifica tu conexión a Internet.", result)
    }

    @Test
    fun `getNetworkErrorMessage returns generic message for null code`() {
        // Act
        val result = errorMessageProvider.getNetworkErrorMessage(null)

        // Assert
        assertEquals("Error de conexión. Por favor, verifica tu conexión a Internet.", result)
    }

    @Test
    fun `getDataErrorMessage returns specific message for price_data context`() {
        // Act
        val result = errorMessageProvider.getDataErrorMessage("price_data")

        // Assert
        assertEquals("No hay datos de precios disponibles para la fecha seleccionada.", result)
    }

    @Test
    fun `getDataErrorMessage returns specific message for device_data context`() {
        // Act
        val result = errorMessageProvider.getDataErrorMessage("device_data")

        // Assert
        assertEquals("No se encontró información del dispositivo.", result)
    }

    @Test
    fun `getDataErrorMessage returns generic message for unknown context`() {
        // Act
        val result = errorMessageProvider.getDataErrorMessage("unknown")

        // Assert
        assertEquals("Los datos solicitados no están disponibles en este momento.", result)
    }

    @Test
    fun `getDataErrorMessage returns generic message for null context`() {
        // Act
        val result = errorMessageProvider.getDataErrorMessage(null)

        // Assert
        assertEquals("Los datos solicitados no están disponibles en este momento.", result)
    }

    @Test
    fun `getValidationErrorMessage returns specific message for date field`() {
        // Act
        val result = errorMessageProvider.getValidationErrorMessage("date")

        // Assert
        assertEquals("La fecha introducida no es válida.", result)
    }

    @Test
    fun `getValidationErrorMessage returns specific message for device_name field`() {
        // Act
        val result = errorMessageProvider.getValidationErrorMessage("device_name")

        // Assert
        assertEquals("El nombre del dispositivo no es válido.", result)
    }

    @Test
    fun `getValidationErrorMessage returns generic message for unknown field`() {
        // Act
        val result = errorMessageProvider.getValidationErrorMessage("unknown")

        // Assert
        assertEquals("Los datos introducidos no son válidos.", result)
    }

    @Test
    fun `getGenericErrorMessage returns consistent message`() {
        // Act
        val result = errorMessageProvider.getGenericErrorMessage()

        // Assert
        assertEquals("Ha ocurrido un error inesperado. Por favor, inténtalo de nuevo.", result)
    }

    @Test
    fun `getTimeoutErrorMessage returns consistent message`() {
        // Act
        val result = errorMessageProvider.getTimeoutErrorMessage()

        // Assert
        assertEquals(
            "La operación ha tardado demasiado tiempo. Por favor, verifica tu conexión a Internet.",
            result
        )
    }

    @Test
    fun `getAuthenticationErrorMessage returns consistent message`() {
        // Act
        val result = errorMessageProvider.getAuthenticationErrorMessage()

        // Assert
        assertEquals("Tu sesión ha expirado. Por favor, vuelve a iniciar sesión.", result)
    }

    @Test
    fun `all error messages are non-empty and user-friendly`() {
        // Act & Assert - verify all methods return non-empty strings
        assertTrue(errorMessageProvider.getNetworkErrorMessage().isNotEmpty())
        assertTrue(errorMessageProvider.getDataErrorMessage().isNotEmpty())
        assertTrue(errorMessageProvider.getValidationErrorMessage().isNotEmpty())
        assertTrue(errorMessageProvider.getGenericErrorMessage().isNotEmpty())
        assertTrue(errorMessageProvider.getTimeoutErrorMessage().isNotEmpty())
        assertTrue(errorMessageProvider.getAuthenticationErrorMessage().isNotEmpty())
    }
}
