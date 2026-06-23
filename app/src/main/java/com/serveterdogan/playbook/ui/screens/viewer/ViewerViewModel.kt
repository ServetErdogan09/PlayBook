package com.serveterdogan.playbook.ui.screens.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serveterdogan.playbook.data.repo.PlaybookRepository
import com.serveterdogan.playbook.domain.model.Position
import com.serveterdogan.playbook.domain.model.Step
import com.serveterdogan.playbook.domain.model.StepType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class ViewerViewModel @Inject constructor(
    private val repository: PlaybookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewerUiState())
    val uiState: StateFlow<ViewerUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null

    /**
     * İzleyici ekranı açıldığında veritabanından set verilerini yükler.
     * initialSetups → playerPositions dönüşümü burada yapılır.
     */
    fun loadPlaybook(playbookId: String) {
        viewModelScope.launch {
            val playbook = repository.getPlaybookWithSteps(playbookId) ?: return@launch
            // Kayıtlı başlangıç pozisyonlarını kullan
            val playerPositions = playbook.initialSetups.associate { it.playerId to it.position }

            _uiState.update {
                it.copy(
                    playbook = playbook,
                    playerPositions = playerPositions,
                    ballPosition = playerPositions["O1"] ?: Position(0.5f, 0.5f),
                    currentDescription = "Hazır. Başlatmak için Play'e basın.",
                    currentStepIndex = 0,
                    isPlaying = false
                )
            }
        }
    }

    fun playClicked() {
        if (_uiState.value.isPlaying) return
        _uiState.update { it.copy(isPlaying = true) }

        playbackJob = viewModelScope.launch {
            val playbook = _uiState.value.playbook ?: return@launch
            val steps = playbook.steps

            // Sona gelindiyse önce başa sar
            var startIndex = _uiState.value.currentStepIndex
            if (startIndex >= steps.size) {
                resetToInitial(playbook.initialSetups.associate { it.playerId to it.position })
                startIndex = 0
                delay(400)
            }

            for (i in startIndex until steps.size) {
                // Her adım başında iptal kontrolü — pause anında durur
                ensureActive()

                val step = steps[i]
                _uiState.update {
                    it.copy(
                        currentStepIndex = i,
                        currentDescription = step.description
                    )
                }

                animateStep(step)

                val pauseMs = (600L / _uiState.value.speedMultiplier).toLong()
                delay(pauseMs)
            }

            _uiState.update {
                it.copy(
                    isPlaying = false,
                    currentDescription = "✓ Set Tamamlandı.",
                    currentStepIndex = steps.size
                )
            }
        }
    }

    fun pauseClicked() {
        playbackJob?.cancel()
        _uiState.update { it.copy(isPlaying = false, currentDescription = "⏸ Duraklatıldı.") }
    }

    /**
     * Başa sar: Oyuncuları initialSetups'a döndür, sayacı sıfırla.
     */
    fun replayClicked() {
        playbackJob?.cancel()
        val playbook = _uiState.value.playbook ?: return
        val initialPositions = playbook.initialSetups.associate { it.playerId to it.position }
        resetToInitial(initialPositions)
    }

    /**
     * Hızı döngüsel olarak değiştirir: 0.5x → 1.0x → 2.0x → 0.5x
     */
    fun toggleSpeed() {
        val currentSpeed = _uiState.value.speedMultiplier
        val nextSpeed = when (currentSpeed) {
            0.5f -> 1.0f
            1.0f -> 2.0f
            else -> 0.5f
        }
        _uiState.update { it.copy(speedMultiplier = nextSpeed) }
    }

    private fun resetToInitial(initialPositions: Map<String, Position>) {
        _uiState.update {
            it.copy(
                currentStepIndex = 0,
                isPlaying = false,
                playerPositions = initialPositions,
                ballPosition = initialPositions["O1"] ?: Position(0.5f, 0.5f),
                currentDescription = "Başa sarıldı."
            )
        }
    }

    /**
     * Bir adımı 60 FPS frame interpolasyon ile canlandırır.
     * Her adım başında _uiState'den TAZE pozisyonları okur.
     */
    private suspend fun animateStep(step: Step) {
        val speed = _uiState.value.speedMultiplier
        val durationMs = (1000L / speed).toLong()
        val frameRate = 60
        val delayMs = (1000L / frameRate / speed).toLong().coerceAtLeast(8L)
        val totalFrames = (durationMs / delayMs).toInt().coerceAtLeast(1)

        //  pozisyonları adım başında oku
        val startPositions = _uiState.value.playerPositions
        val startBallPos = _uiState.value.ballPosition

        when (step.type) {
            StepType.PASS -> {
                val fromPos = startPositions[step.primaryPlayerId] ?: return
                val toPos = startPositions[step.targetPlayerId] ?: return

                // Top pas verenin elinden alıcıya animasyonla gider
                for (frame in 1..totalFrames) {
                    currentCoroutineContext().ensureActive() // pause anında anında dur
                    val t = frame.toFloat() / totalFrames
                    val newBallX = fromPos.x + (toPos.x - fromPos.x) * t
                    val newBallY = fromPos.y + (toPos.y - fromPos.y) * t
                    _uiState.update { it.copy(ballPosition = Position(newBallX, newBallY)) }
                    delay(delayMs)
                }
                // ✅ Animasyon bitince top hedef oyuncuya yapışır
                _uiState.update { it.copy(ballPosition = toPos) }
            }

            StepType.MOVE -> {
                val fromPos = startPositions[step.primaryPlayerId] ?: return
                val toPos = step.targetPosition ?: return

                // Top bu oyuncunun üzerindeyse onunla beraber hareket etsin
                val ballWithPlayer = abs(startBallPos.x - fromPos.x) < 0.06f &&
                        abs(startBallPos.y - fromPos.y) < 0.06f

                for (frame in 1..totalFrames) {
                    currentCoroutineContext().ensureActive()
                    val t = frame.toFloat() / totalFrames
                    val newX = fromPos.x + (toPos.x - fromPos.x) * t
                    val newY = fromPos.y + (toPos.y - fromPos.y) * t
                    val newPos = Position(newX, newY)

                    val updatedMap = _uiState.value.playerPositions.toMutableMap()
                    updatedMap[step.primaryPlayerId] = newPos

                    _uiState.update {
                        it.copy(
                            playerPositions = updatedMap,
                            ballPosition = if (ballWithPlayer) newPos else it.ballPosition
                        )
                    }
                    delay(delayMs)
                }
            }

            StepType.SCREEN -> {
                val fromPos = startPositions[step.primaryPlayerId] ?: return
                // Perde: perdeyi yapan oyuncu, hedef oyuncunun yanına gider
                val targetPlayerPos = if (step.targetPlayerId != null) {
                    startPositions[step.targetPlayerId] ?: return
                } else {
                    step.targetPosition ?: return
                }
                // Hedef oyuncunun hemen yanına kon (hafif offset)
                val toPos = Position(targetPlayerPos.x + 0.07f, targetPlayerPos.y)

                for (frame in 1..totalFrames) {
                    currentCoroutineContext().ensureActive()
                    val t = frame.toFloat() / totalFrames
                    val newX = fromPos.x + (toPos.x - fromPos.x) * t
                    val newY = fromPos.y + (toPos.y - fromPos.y) * t

                    val updatedMap = _uiState.value.playerPositions.toMutableMap()
                    updatedMap[step.primaryPlayerId] = Position(newX, newY)
                    _uiState.update { it.copy(playerPositions = updatedMap) }
                    delay(delayMs)
                }
            }

            StepType.SHOOT -> {
                val fromPos = startPositions[step.primaryPlayerId] ?: return
                val hoopPos = Position(0.5f, 0.03f) // Pota (sahanın üst ortası)

                for (frame in 1..totalFrames) {
                    currentCoroutineContext().ensureActive()
                    val t = frame.toFloat() / totalFrames
                    val newBallX = fromPos.x + (hoopPos.x - fromPos.x) * t
                    val newBallY = fromPos.y + (hoopPos.y - fromPos.y) * t
                    _uiState.update { it.copy(ballPosition = Position(newBallX, newBallY)) }
                    delay(delayMs)
                }
                // ✅ Şut sonrası top potaya yapışır
                _uiState.update { it.copy(ballPosition = hoopPos) }
            }
        }
    }
}