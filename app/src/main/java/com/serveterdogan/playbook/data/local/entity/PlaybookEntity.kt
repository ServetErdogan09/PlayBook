package com.serveterdogan.playbook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room tablosuna karşılık gelen Playbook Entity.
 * List<Player> ve List<InitialSetup> gibi karmaşık alanlar JSON string olarak saklanır.
 * (TypeConverter ile otomatik dönüşüm sağlanır)
 */
@Entity(tableName = "playbooks")
data class PlaybookEntity(
    @PrimaryKey val id: String,
    val name: String,
    val playersJson: String,        // List<Player> → JSON String
    val initialSetupsJson: String   // List<InitialSetup> → JSON String
)
