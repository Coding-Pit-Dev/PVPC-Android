package com.codingpit.pvpcplanner.ui.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val repository: Repository,
): ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun getPrices() {
        viewModelScope.launch {
            _state.update { HomeState.Loading }

            val data =
                runCatching {
                    withContext(Dispatchers.IO) {
                        repository.getPrices()
                    }
                }
            if (data.isSuccess) {
                _state.update { HomeState.Success(data.getOrThrow()) }
            } else {
                _state.update { HomeState.Error(data.exceptionOrNull()?.message.orEmpty()) }
            }
        }
    }
}
