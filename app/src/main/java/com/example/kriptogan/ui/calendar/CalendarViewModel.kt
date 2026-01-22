package com.example.kriptogan.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kriptogan.data.repository.SettingsRepository
import com.example.kriptogan.data.repository.WorkDayRepository
import com.example.kriptogan.util.DateUtils
import com.example.kriptogan.util.TimeCalculationUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val workDaysWithEntries: List<com.example.kriptogan.data.repository.WorkDayWithEntries> = emptyList(),
    val monthlyCalculation: TimeCalculationUtils.MonthlyCalculation? = null,
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val workDayRepository: WorkDayRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    
    // Observe work days changes for the current month
    val uiState: StateFlow<CalendarUiState> = combine(
        _currentMonth,
        settingsRepository.getSettingsFlow()
    ) { yearMonth, settings ->
        Pair(yearMonth, settings)
    }.flatMapLatest { (yearMonth, settings) ->
        if (settings == null) {
            flowOf(CalendarUiState(currentMonth = yearMonth, isLoading = false))
        } else {
            workDayRepository.getWorkDaysInDateRange(
                yearMonth.atDay(1),
                yearMonth.atEndOfMonth()
            ).map { workDays ->
                // Filter out future days
                val today = LocalDate.now()
                val filteredWorkDays = workDays.filter { it.workDay.date <= today }
                
                val monthlyCalc = TimeCalculationUtils.calculateMonthly(filteredWorkDays, settings)
                
                CalendarUiState(
                    currentMonth = yearMonth,
                    workDaysWithEntries = workDays,
                    monthlyCalculation = monthlyCalc,
                    isLoading = false
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )

    init {
        // Trigger initial load
        _currentMonth.value = YearMonth.now()
    }

    fun loadMonth(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
    }

    fun navigateToPreviousMonth() {
        val previousMonth = _currentMonth.value.minusMonths(1)
        loadMonth(previousMonth)
    }

    fun navigateToNextMonth() {
        val nextMonth = _currentMonth.value.plusMonths(1)
        val today = YearMonth.now()
        // Only allow navigation to current month or past months
        if (nextMonth <= today) {
            loadMonth(nextMonth)
        }
    }

    fun refresh() {
        loadMonth(_currentMonth.value)
    }
}
