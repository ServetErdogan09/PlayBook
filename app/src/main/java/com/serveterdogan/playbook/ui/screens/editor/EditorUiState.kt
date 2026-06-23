package com.serveterdogan.playbook.ui.screens.editor

import com.serveterdogan.playbook.domain.model.Position
import com.serveterdogan.playbook.domain.model.Step
import com.serveterdogan.playbook.domain.model.StepType

/**
 * Editördeki etkileşim aşamalarını tutar.
 * Örn: "Pas" butonuna basıldıysa, sistem bir oyuncu seçilmesini bekler.
 */
enum class InteractionState {
    IDLE,                  // Boşta, hiçbir butona basılmadı
    SELECTING_PRIMARY,     // Eylemi yapacak adam seçiliyor (Örn: Pası verecek kişi)
    SELECTING_TARGET,      // Eylemin hedefi seçiliyor (Örn: Pası alacak kişi)
    SELECTING_LOCATION     // Hedef koordinat seçiliyor (Koşu için sahanın bir noktası)
}

data class EditorUiState(
    val playbookId: String = "new_playbook",
    val playbookName: String = "Yeni Set",
    
    // Sahadaki oyuncuların anlık konumları (Sürükle bırak için)
    val playerPositions: Map<String, Position> = mapOf(
        "O1" to Position(0.5f, 0.2f),
        "O2" to Position(0.8f, 0.4f),
        "O3" to Position(0.2f, 0.4f),
        "O4" to Position(0.3f, 0.8f),
        "O5" to Position(0.5f, 0.6f)
    ),
    
    // Zaman çizelgesine eklenmiş adımlar
    val steps: List<Step> = emptyList(),

    // Kullanıcının eylem (Step) ekleme süreci
    val interactionState: InteractionState = InteractionState.IDLE,
    val selectedAction: StepType? = null,
    val selectedPrimaryPlayerId: String? = null,
    val selectedTargetPlayerId: String? = null,
    
    // UI'da kullanıcıya ne yapması gerektiğini söyleyen mesaj
    val hintMessage: String = "Oyuncuları yerleştir veya bir eylem seç."
)
