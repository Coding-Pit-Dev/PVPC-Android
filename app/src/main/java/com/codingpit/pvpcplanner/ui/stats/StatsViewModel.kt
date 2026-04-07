package com.codingpit.pvpcplanner.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.error.handleErrors
import com.codingpit.pvpcplanner.domain.usecase.GetPriceHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsViewModel
    @Inject
    constructor(
        private val getPriceHistory: GetPriceHistory,
        private val errorHandler: ErrorHandler,
        private val coroutineDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        val state =
            flow {
                val result = getPriceHistory()
                emit(result.getOrThrow())
            }.map { summaries ->
                StatsState.Success(summaries = summaries) as StatsState
            }.handleErrors(errorHandler, "stats_data", StatsState.Factory)
                .flowOn(coroutineDispatcher)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsState.Loading)
    }
