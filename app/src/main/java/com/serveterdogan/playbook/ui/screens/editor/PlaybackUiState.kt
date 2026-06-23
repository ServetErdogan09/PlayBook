package com.serveterdogan.playbook.ui.screens.editor

import com.serveterdogan.playbook.domain.model.Position

data class PlaybackUiState(
    val isPlaying : Boolean, // playa basıldı mı
    val currentStepIndex : Int, // kaçıncı adımdayız
    val currentDescription : String, // adım açıkalmaları
    val playerPositions : Map<String , Position>,
    val ballPosition : Position
)