package com.codingpit.pvpcplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.domain.usecase.GetPrices
import com.codingpit.pvpcplanner.utils.DateChecker
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
    dateChecker: DateChecker
) : ViewModel() {

    private val selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(dateChecker.getDefaultDate())
    val state: StateFlow<HomeState> = selectedDate
        .map {
            getPrices(it.toParsedDate())
        }
        .map {
            HomeState.Success(
                currentDate = selectedDate.value.toParsedDate(),
                pvpcEntries = it.getOrThrow(),
                nextDateEnabled = dateChecker.checkValidDate(selectedDate.value),
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
