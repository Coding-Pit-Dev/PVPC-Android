package com.codingpit.pvpcplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.domain.usecase.GetPrices
import com.codingpit.pvpcplanner.domain.usecase.date.GetDefaultDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalHour
import com.codingpit.pvpcplanner.domain.usecase.date.IsValidDate
import com.codingpit.pvpcplanner.utils.toParsedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getPrices: GetPrices,
    getDefaultDate: GetDefaultDate,
    isValidDate: IsValidDate,
    getLocalHour: GetLocalHour,
    getLocalDate: GetLocalDate
) : ViewModel() {

    private val selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(getDefaultDate())
    val state: StateFlow<HomeState> = selectedDate
        .map {
            getPrices(it.toParsedDate())
        }
        .map {
            val currentHour = getLocalHour()
            HomeState.Success(
                selectedDate = selectedDate.value.toParsedDate(),
                pvpcEntries = it.getOrThrow(),
                nextDateEnabled = isValidDate(selectedDate.value),
                currentPrice = it.getOrThrow().first { it.startHour == currentHour }.pcb,
                currentHour = currentHour,
                currentDate =  getLocalDate().toParsedDate()
            )
        }
        .catch { HomeState.Error(it.message.orEmpty()) }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, HomeState.Loading)

    fun onPreviewClicked() {
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
