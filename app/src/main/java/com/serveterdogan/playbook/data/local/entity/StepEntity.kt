package com.serveterdogan.playbook.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Her adım (Step) ayrı bir tablo satırıdır.
 * Hangi Playbook'a ait olduğu "playbookId" ile bağlanır.
 * Position bilgisi x ve y olarak iki ayrı Float kolona açılır.
 */
@Entity(
    tableName = "steps",
    foreignKeys = [
        ForeignKey(
            entity = PlaybookEntity::class,
            parentColumns = ["id"],
            childColumns = ["playbookId"],
            onDelete = ForeignKey.CASCADE // Playbook silinince adımlar da silinir
        )
    ],
    indices = [Index("playbookId")]
)
data class StepEntity(
    @PrimaryKey val id: String,
    val playbookId: String,          // Bu adımın ait olduğu Playbook
    val type: String,                // StepType enum → String (PASS, MOVE, SCREEN, SHOOT)
    val primaryPlayerId: String,     // Eylemi yapan oyuncu
    val targetPlayerId: String?,     // Eylemin hedefi (opsiyonel)
    val targetPositionX: Float?,     // Hedef koordinat X (opsiyonel)
    val targetPositionY: Float?,     // Hedef koordinat Y (opsiyonel)
    val description: String,         // UI'da gösterilecek metin
    val stepOrder: Int               // Adımların sıralaması (1, 2, 3...)
)
