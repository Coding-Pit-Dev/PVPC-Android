package com.codingpit.pvpcplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.error.handleErrors
import com.codingpit.pvpcplanner.domain.usecase.GetPrices
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.date.GetDefaultDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalHour
import com.codingpit.pvpcplanner.domain.usecase.date.IsValidDate
import com.codingpit.pvpcplanner.utils.toParsedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCaseProvider: HomeUseCaseProvider,
    errorHandler: ErrorHandler,
    coroutineDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(useCaseProvider.getDefaultDate())
    val state: StateFlow<HomeState> =
        selectedDate
            .map {
                useCaseProvider.getPrices(it.toParsedDate())
            }
            .combine(useCaseProvider.getSettings()) { prices, settings ->
                prices to settings
            }
            .map { (it, settings) ->
                val currentHour = useCaseProvider.getLocalHour()
                HomeState.Success(
                    selectedDate = selectedDate.value.toParsedDate(),
                    pvpcEntries = it.getOrThrow(),
                    nextDateEnabled = useCaseProvider.isValidDate(selectedDate.value),
                    currentPrice = it.getOrThrow().first { it.startHour == currentHour }.pcb,
                    currentHour = currentHour,
                    currentDate = useCaseProvider.getLocalDate().toParsedDate(),
                    timeFormat = settings.timeFormat,
                )
            }
            .handleErrors(errorHandler, "home_data", HomeState.Factory)
            .flowOn(coroutineDispatcher)
            .stateIn(viewModelScope, SharingStarted.Eagerly, HomeState.Loading)

    fun onPreviousClicked() {
        viewModelScope.launch {
            val currentDate = selectedDate.value

            selectedDate.update {
                currentDate.minusDays(1)
            }
        }
    }

    fun onNextClicked() {
        viewModelScope.launch {
            val currentDate = selectedDate.value

            selectedDate.update {
                currentDate.plusDays(1)
            }
        }
    }
}
