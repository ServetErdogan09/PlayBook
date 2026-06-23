package com.serveterdogan.playbook.ui.screens.playsetlist

import com.serveterdogan.playbook.domain.model.Playbook

sealed class PlayBookListUiState {
    object  Loading : PlayBookListUiState()
    data class Success(val playlists : List<Playbook>) : PlayBookListUiState()
    data class Error(val message : String) : PlayBookListUiState()
}