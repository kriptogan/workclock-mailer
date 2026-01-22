package com.example.kriptogan.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kriptogan.data.repository.SettingsRepository
import com.example.kriptogan.data.repository.WorkDayRepository
import com.example.kriptogan.util.TimeCalculationUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth

data class MonthSummary(
    val yearMonth: YearMonth,
    val totalWorked: Double,
    val totalExpected: Int,
    val netOvertime: Double,
    val netMissing: Double
)

data class HistoricalMonthsUiState(
    val months: List<MonthSummary> = emptyList(),
    val isLoading: Boolean = false
)

class HistoricalMonthsViewModel(
    private val workDayRepository: WorkDayRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoricalMonthsUiState())
    val uiState: StateFlow<HistoricalMonthsUiState> = _uiState.asStateFlow()

    init {
        loadMonths()
    }

    private fun loadMonths() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val monthsWithData = workDayRepository.getMonthsWithData()
                val settings = settingsRepository.getSettings()
                
                if (settings != null) {
                    val monthSummaries = monthsWithData.map { dbYearMonth ->
                        val workDays = workDayRepository.getWorkDaysForMonth(
                            dbYearMonth.year,
                            dbYearMonth.month
                        )
                        val calculation = TimeCalculationUtils.calculateMonthly(workDays, settings)
                        
                        MonthSummary(
                            yearMonth = YearMonth.of(dbYearMonth.year, dbYearMonth.month),
                            totalWorked = calculation.totalWorked,
                            totalExpected = calculation.totalExpected,
                            netOvertime = calculation.netOvertime,
                            netMissing = calculation.netMissing
                        )
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        months = monthSummaries,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun refresh() {
        loadMonths()
    }
}
