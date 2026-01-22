package com.example.kriptogan.ui.daydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kriptogan.data.model.*
import com.example.kriptogan.data.repository.SettingsRepository
import com.example.kriptogan.data.repository.WorkDayRepository
import com.example.kriptogan.util.TimeCalculationUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

data class DayDetailUiState(
    val date: LocalDate,
    val workDay: WorkDay? = null,
    val timeEntries: List<TimeEntry> = emptyList(),
    val dayType: DayType = DayType.NORMAL,
    val calculation: TimeCalculationUtils.DailyCalculation? = null,
    val isTimerRunning: Boolean = false,
    val timerStartTime: LocalTime? = null,
    val isLoading: Boolean = false
)

class DayDetailViewModel(
    private val date: LocalDate,
    private val workDayRepository: WorkDayRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DayDetailUiState(date = date))
    val uiState: StateFlow<DayDetailUiState> = _uiState.asStateFlow()

    init {
        loadDayData()
    }

    private fun loadDayData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                var workDay = workDayRepository.getWorkDayByDate(date)
                
                // If work day doesn't exist, create it with NORMAL type
                if (workDay == null) {
                    val newWorkDay = WorkDay(
                        date = date,
                        dayType = DayType.NORMAL
                    )
                    val workDayId = workDayRepository.insertOrUpdateWorkDay(newWorkDay)
                    workDay = newWorkDay.copy(id = workDayId)
                }
                
                val timeEntries = workDayRepository.getWorkDaysInDateRangeSync(date, date)
                    .firstOrNull()?.timeEntries ?: emptyList()
                
                val settings = settingsRepository.getSettings()
                val calculation = if (settings != null) {
                    val workDayWithEntries = com.example.kriptogan.data.repository.WorkDayWithEntries(
                        workDay,
                        timeEntries
                    )
                    TimeCalculationUtils.calculateDaily(workDayWithEntries, settings)
                } else null

                _uiState.value = _uiState.value.copy(
                    workDay = workDay,
                    timeEntries = timeEntries,
                    dayType = workDay.dayType,
                    calculation = calculation,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun startTimer() {
        _uiState.value = _uiState.value.copy(
            isTimerRunning = true,
            timerStartTime = LocalTime.now()
        )
    }

    fun stopTimer() {
        val currentState = _uiState.value
        if (currentState.isTimerRunning && currentState.timerStartTime != null) {
            viewModelScope.launch {
                val endTime = LocalTime.now()
                val settings = settingsRepository.getSettings()
                val breakMinutes = settings?.breakMinutes ?: 30
                
                // Get or create work day
                val workDay = currentState.workDay ?: run {
                    val newWorkDay = WorkDay(
                        date = date,
                        dayType = DayType.NORMAL
                    )
                    val workDayId = workDayRepository.insertOrUpdateWorkDay(newWorkDay)
                    newWorkDay.copy(id = workDayId)
                }
                
                val timeEntry = TimeEntry(
                    workDayId = workDay.id,
                    startTime = currentState.timerStartTime!!,
                    endTime = endTime,
                    breakMinutes = breakMinutes
                )
                workDayRepository.insertTimeEntry(timeEntry)
                
                _uiState.value = _uiState.value.copy(
                    isTimerRunning = false,
                    timerStartTime = null
                )
                loadDayData()
            }
        }
    }

    fun addTimeEntry(startTime: LocalTime, endTime: LocalTime, breakMinutes: Int) {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings()
            val breakTime = breakMinutes.takeIf { it >= 0 } ?: (settings?.breakMinutes ?: 30)
            
            val workDay = _uiState.value.workDay ?: run {
                val newWorkDay = WorkDay(date = date, dayType = DayType.NORMAL)
                val workDayId = workDayRepository.insertOrUpdateWorkDay(newWorkDay)
                newWorkDay.copy(id = workDayId)
            }
            
            val timeEntry = TimeEntry(
                workDayId = workDay.id,
                startTime = startTime,
                endTime = endTime,
                breakMinutes = breakTime
            )
            workDayRepository.insertTimeEntry(timeEntry)
            loadDayData()
        }
    }

    fun updateTimeEntry(timeEntry: TimeEntry, startTime: LocalTime, endTime: LocalTime, breakMinutes: Int) {
        viewModelScope.launch {
            val updated = timeEntry.copy(
                startTime = startTime,
                endTime = endTime,
                breakMinutes = breakMinutes
            )
            workDayRepository.updateTimeEntry(updated)
            loadDayData()
        }
    }

    fun deleteTimeEntry(timeEntry: TimeEntry) {
        viewModelScope.launch {
            workDayRepository.deleteTimeEntry(timeEntry)
            loadDayData()
        }
    }

    fun updateDayType(dayType: DayType) {
        viewModelScope.launch {
            val workDay = _uiState.value.workDay ?: WorkDay(
                date = date,
                dayType = dayType
            )
            val updated = workDay.copy(dayType = dayType)
            workDayRepository.insertOrUpdateWorkDay(updated)
            loadDayData()
        }
    }

    fun getElapsedTime(): Long {
        val currentState = _uiState.value
        return if (currentState.isTimerRunning && currentState.timerStartTime != null) {
            java.time.Duration.between(currentState.timerStartTime, LocalTime.now()).toMinutes()
        } else {
            0
        }
    }
}
