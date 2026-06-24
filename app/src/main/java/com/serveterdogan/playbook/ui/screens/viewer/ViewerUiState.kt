package com.serveterdogan.playbook.ui.screens.viewer

import com.serveterdogan.playbook.domain.model.Playbook
import com.serveterdogan.playbook.domain.model.Position

data class ViewerUiState(
    val playbook: Playbook? = null,
    val isPlaying: Boolean = false,
    val currentStepIndex: Int = 0,
    val currentDescription: String = "",
    val playerPositions: Map<String, Position> = emptyMap(),
    val ballPosition: Position = Position(0.5f, 0.5f),
    // 0.5x → yavaş, 1.0x → normal, 2.0x → hızlı
    val speedMultiplier: Float = 1.0f,
    
    // Tema Renkleri
    val courtColor: String = "#121A2A",
    val playerColor: String = "#151C2E",
    val ballColor: String = "#F28C38"
)
