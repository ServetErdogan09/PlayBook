package com.serveterdogan.playbook.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serveterdogan.playbook.data.local.datastore.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val courtColor: StateFlow<String> = settingsRepository.courtColor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsRepository.DEFAULT_COURT_COLOR
    )

    val playerColor: StateFlow<String> = settingsRepository.playerColor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsRepository.DEFAULT_PLAYER_COLOR
    )

    val ballColor: StateFlow<String> = settingsRepository.ballColor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsRepository.DEFAULT_BALL_COLOR
    )

    fun updateCourtColor(colorHex: String) {
        viewModelScope.launch {
            settingsRepository.setCourtColor(colorHex)
        }
    }

    fun updatePlayerColor(colorHex: String) {
        viewModelScope.launch {
            settingsRepository.setPlayerColor(colorHex)
        }
    }

    fun updateBallColor(colorHex: String) {
        viewModelScope.launch {
            settingsRepository.setBallColor(colorHex)
        }
    }


    fun resetDefault(){
        viewModelScope.launch {
          settingsRepository.resetDefaultColor()
        }
    }
}
