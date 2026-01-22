package com.example.kriptogan.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kriptogan.data.model.AppSettings
import com.example.kriptogan.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek

data class SettingsUiState(
    val workDays: Set<DayOfWeek> = emptySet(),
    val workHoursPerDay: Int = 9,
    val breakMinutes: Int = 30,
    val holidayEveningHours: Int = 6,
    val semiDayOffHours: Int = 4,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveMessage: String? = null
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private var autoSaveJob: Job? = null
    private var hasUnsavedChanges = false

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val settings = settingsRepository.getSettings()
                if (settings != null) {
                    _uiState.value = _uiState.value.copy(
                        workDays = settings.workDays,
                        workHoursPerDay = settings.workHoursPerDay,
                        breakMinutes = settings.breakMinutes,
                        holidayEveningHours = settings.holidayEveningHours,
                        semiDayOffHours = settings.semiDayOffHours,
                        isLoading = false
                    )
                } else {
                    // Initialize default settings if none exist
                    settingsRepository.initializeDefaultSettings()
                    loadSettings() // Reload after initialization
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun toggleWorkDay(day: DayOfWeek) {
        val currentDays = _uiState.value.workDays.toMutableSet()
        if (currentDays.contains(day)) {
            currentDays.remove(day)
        } else {
            currentDays.add(day)
        }
        _uiState.value = _uiState.value.copy(workDays = currentDays)
        scheduleAutoSave()
    }

    fun updateWorkHoursPerDay(hours: Int) {
        if (hours in 1..24) {
            _uiState.value = _uiState.value.copy(workHoursPerDay = hours)
            scheduleAutoSave()
        }
    }

    fun updateBreakMinutes(minutes: Int) {
        if (minutes in 0..480) {
            _uiState.value = _uiState.value.copy(breakMinutes = minutes)
            scheduleAutoSave()
        }
    }

    fun updateHolidayEveningHours(hours: Int) {
        if (hours in 0..24) {
            _uiState.value = _uiState.value.copy(holidayEveningHours = hours)
            scheduleAutoSave()
        }
    }
    
    fun updateSemiDayOffHours(hours: Int) {
        if (hours in 0..24) {
            _uiState.value = _uiState.value.copy(semiDayOffHours = hours)
            scheduleAutoSave()
        }
    }
    
    private fun scheduleAutoSave() {
        hasUnsavedChanges = true
        // Cancel previous auto-save job
        autoSaveJob?.cancel()
        // Schedule new auto-save after 1 second of no changes
        autoSaveJob = viewModelScope.launch {
            delay(1000) // Wait 1 second after last change
            if (hasUnsavedChanges) {
                saveSettingsSilently()
            }
        }
    }
    
    private suspend fun saveSettingsSilently() {
        try {
            val currentState = _uiState.value
            val existingSettings = settingsRepository.getSettings()
                val settings = AppSettings(
                    id = existingSettings?.id ?: 1,
                    workDays = currentState.workDays,
                    workHoursPerDay = currentState.workHoursPerDay,
                    breakMinutes = currentState.breakMinutes,
                    holidayEveningHours = currentState.holidayEveningHours,
                    semiDayOffHours = currentState.semiDayOffHours
                )
            settingsRepository.updateSettings(settings)
            hasUnsavedChanges = false
        } catch (e: Exception) {
            // Silently fail - user can manually save if needed
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            // Cancel any pending auto-save
            autoSaveJob?.cancel()
            _uiState.value = _uiState.value.copy(isSaving = true, saveMessage = null)
            try {
                val currentState = _uiState.value
                // Get existing settings to preserve the ID, or use default ID of 1
                val existingSettings = settingsRepository.getSettings()
                val settings = AppSettings(
                    id = existingSettings?.id ?: 1,
                    workDays = currentState.workDays,
                    workHoursPerDay = currentState.workHoursPerDay,
                    breakMinutes = currentState.breakMinutes,
                    holidayEveningHours = currentState.holidayEveningHours,
                    semiDayOffHours = currentState.semiDayOffHours
                )
                settingsRepository.updateSettings(settings)
                hasUnsavedChanges = false
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveMessage = "Settings saved successfully"
                )
                // Clear message after 3 seconds
                delay(3000)
                _uiState.value = _uiState.value.copy(saveMessage = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveMessage = "Error saving settings: ${e.message}"
                )
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        // Save any unsaved changes when ViewModel is cleared
        if (hasUnsavedChanges) {
            viewModelScope.launch {
                saveSettingsSilently()
            }
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(saveMessage = null)
    }
}
