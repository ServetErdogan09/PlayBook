package com.serveterdogan.playbook.ui.screens.editor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serveterdogan.playbook.data.repo.PlaybookRepository
import com.serveterdogan.playbook.domain.model.InitialSetup
import com.serveterdogan.playbook.domain.model.Playbook
import com.serveterdogan.playbook.domain.model.Player
import com.serveterdogan.playbook.domain.model.PlayerRole
import com.serveterdogan.playbook.domain.model.Position
import com.serveterdogan.playbook.domain.model.Step
import com.serveterdogan.playbook.domain.model.StepType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: PlaybookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val _snackbar = MutableSharedFlow<String>()
    val snackbar = _snackbar.asSharedFlow()

    /**
     * Oyuncuyu sahada sürüklerken çağrılır.
     */
    fun updatePlayerPosition(playerId: String, newX: Float, newY: Float) {
        // Ekranın dışına çıkmasını engellemek için 0.0 ile 1.0 arasına sıkıştır (coerceIn)
        val clampedX = newX.coerceIn(0.0f, 1.0f)
        val clampedY = newY.coerceIn(0.0f, 1.0f)

        _uiState.update { currentState ->
            val updatedPositions = currentState.playerPositions.toMutableMap()
            updatedPositions[playerId] = Position(clampedX, clampedY)
            currentState.copy(playerPositions = updatedPositions)
        }
    }

    /**
     * Alttaki "Pas", "Koşu", "Perde" butonlarına basıldığında çağrılır.
     */
    fun onActionSelected(action: StepType) {

        val actionText = when(action) {
            StepType.PASS -> "Pası verecek oyuncuyu"
            StepType.MOVE -> "Koşuyu yapacak oyuncuyu"
            StepType.SCREEN -> "Perdeye gidecek oyuncuyu"
            StepType.SHOOT -> "Şutu atacak oyuncuyu"
        }

        _uiState.update {
            it.copy(
                interactionState = InteractionState.SELECTING_PRIMARY,
                selectedAction = action,
                selectedPrimaryPlayerId = null,
                selectedTargetPlayerId = null,
                hintMessage = actionText
            )
        }
    }

    /**
     * Saha üzerindeki bir oyuncuya tıklandığında çağrılır (Eylem oluşturma menüsü için)
     */
    fun onPlayerClicked(playerId: String) {
        val currentState = _uiState.value

        when (currentState.interactionState) {
            InteractionState.SELECTING_PRIMARY -> {
                // 1. AŞAMA: Eylemi yapacak kişiyi seçti
                
                // Eğer ŞUT seçildiyse, hedef seçmeye gerek yok. Direkt adımı oluştur.
                if (currentState.selectedAction == StepType.SHOOT) {
                    createStepAndReset(
                        primaryId = playerId,
                        targetId = null,
                        targetPos = null,
                        desc = "$playerId ŞUT Atıyor"
                    )
                    return
                }

                // Eğer Hareket (MOVE) seçildiyse hedef konum (saha noktası) seçmesi lazım
                if (currentState.selectedAction == StepType.MOVE) {
                    _uiState.update {
                        it.copy(
                            interactionState = InteractionState.SELECTING_LOCATION,
                            selectedPrimaryPlayerId = playerId,
                            hintMessage = "Saha üzerinde koşulacak boş bir noktaya tıklayın."
                        )
                    }
                    return
                }

                // Pas veya Perde ise 2. bir oyuncu (hedef) seçmesi lazım
                _uiState.update {
                    it.copy(
                        interactionState = InteractionState.SELECTING_TARGET,
                        selectedPrimaryPlayerId = playerId,
                        hintMessage = "Hedef oyuncuyu seçin."
                    )
                }
            }

            InteractionState.SELECTING_TARGET -> {
                // 2. AŞAMA: Hedef oyuncuyu seçti (Pas veya Perde için)
                val primaryId = currentState.selectedPrimaryPlayerId ?: return
                
                // Kendi kendine pas atamaz
                if (primaryId == playerId) return

                val actionName = if (currentState.selectedAction == StepType.PASS) "PAS" else "PERDE"
                
                createStepAndReset(
                    primaryId = primaryId,
                    targetId = playerId,
                    targetPos = null,
                    desc = "$primaryId -> $playerId $actionName"
                )
            }

            else -> {
                // Boşta tıklandıysa bir şey yapma
            }
        }
    }

    /**
     * Saha üzerinde boş bir yere tıklandığında (Koşu/Move işlemi için)
     */
    fun onCourtClicked(x: Float, y: Float) {
        val currentState = _uiState.value
        
        if (currentState.interactionState == InteractionState.SELECTING_LOCATION) {
            val primaryId = currentState.selectedPrimaryPlayerId ?: return
            
            createStepAndReset(
                primaryId = primaryId,
                targetId = null,
                targetPos = Position(x, y),
                desc = "$primaryId Boşluğa KOŞU"
            )
        }
    }

    /**
     * Seçim iptal edildiğinde
     */
    fun cancelInteraction() {
        _uiState.update {
            it.copy(
                interactionState = InteractionState.IDLE,
                selectedAction = null,
                selectedPrimaryPlayerId = null,
                selectedTargetPlayerId = null,
                hintMessage = "Oyuncuları yerleştir veya bir eylem seç."
            )
        }
    }

    /**
     * Zaman çizelgesinden bir adımı siler
     */
    fun removeStep(stepId: String) {
        _uiState.update {
            it.copy(steps = it.steps.filterNot { step -> step.id == stepId })
        }
    }

    private fun createStepAndReset(
        primaryId: String,
        targetId: String?,
        targetPos: Position?,
        desc: String
    ) {
        val action = _uiState.value.selectedAction ?: return
        
        val newStep = Step(
            id = UUID.randomUUID().toString(),
            type = action,
            primaryPlayerId = primaryId,
            targetPlayerId = targetId,
            targetPosition = targetPos,
            description = desc
        )

        _uiState.update {
            val updatedSteps = it.steps.toMutableList().apply { add(newStep) }
            it.copy(
                steps = updatedSteps,
                interactionState = InteractionState.IDLE,
                selectedAction = null,
                selectedPrimaryPlayerId = null,
                selectedTargetPlayerId = null,
                hintMessage = "Adım eklendi! Yeni eylem seçebilirsiniz."
            )
        }
    }

    /**
     * Veritabanına kaydetme işlemi
     */
    fun savePlaybook(name: String) {
        viewModelScope.launch {
            val currentState = _uiState.value


            if(currentState.steps.isEmpty()){
                _snackbar.emit("Lütfen kaydetmeden önce en az bir adım çizin!")
                return@launch // aşağıdaki kodlara gelme
            }
            Log.d("savePlaybook","savePlaybook: ${currentState.steps.size}")
            
            // Ekrandaki oyunculardan liste oluştur
            val players = currentState.playerPositions.keys.map { id ->
                // Şimdilik varsayılan bir rol atıyoruz (O1 -> PG vb.)
                val role = when (id) {
                    "O1" -> PlayerRole.PG
                    "O2" -> PlayerRole.SG
                    "O3" -> PlayerRole.SF
                    "O4" -> PlayerRole.PF
                    else -> PlayerRole.C
                }
                Player(id = id, role = role)
            }
            
            val initialSetups = currentState.playerPositions.map { (id, pos) ->
                InitialSetup(playerId = id, position = pos)
            }
            
            val newId = if (currentState.playbookId == "new_playbook") UUID.randomUUID().toString() else currentState.playbookId
            
            val playbook = Playbook(
                id = newId,
                name = name,
                players = players,
                initialSetups = initialSetups,
                steps = currentState.steps
            )
            
            repository.savePlaybook(playbook)
            
            _uiState.update { 
                it.copy(playbookId = newId, playbookName = name, hintMessage = "Başarıyla Kaydedildi!") 
            }
        }
    }

    /**
     * Veritabanından mevcut bir taktiği yükleme işlemi
     */
    fun loadPlaybook(playbookId: String) {
        viewModelScope.launch {
            val playbook = repository.getPlaybookWithSteps(playbookId) ?: return@launch
            
            val positions = playbook.initialSetups.associate { it.playerId to it.position }
            
            _uiState.update {
                it.copy(
                    playbookId = playbook.id,
                    playbookName = playbook.name,
                    playerPositions = positions,
                    steps = playbook.steps,
                    hintMessage = "Taktik yüklendi."
                )
            }
        }
    }
}