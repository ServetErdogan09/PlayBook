package com.serveterdogan.playbook.ui.screens.playsetlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serveterdogan.playbook.data.repo.PlaybookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlaybookListViewModel @Inject constructor(
    private val repository: PlaybookRepository
) : ViewModel() {

    // Veritabanından gelen Flow'u doğrudan UiState'e dönüştürüyoruz
    val uiState: StateFlow<PlayBookListUiState> = repository.getAllPlayBook()
        .map { playbooks ->
            PlayBookListUiState.Success(playbooks)  // veri geldiyse success döndür
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Ekran kapandıktan 5 sn sonra flow'u durdur
            initialValue = PlayBookListUiState.Loading
        )
}
