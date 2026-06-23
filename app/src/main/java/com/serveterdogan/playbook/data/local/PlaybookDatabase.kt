package com.serveterdogan.playbook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.serveterdogan.playbook.data.local.entity.PlaybookEntity
import com.serveterdogan.playbook.data.local.entity.StepEntity

/**
 * Uygulamanın tek veritabanı sınıfı.
 * entities → hangi tablolar var
 * version → şema değişince artırılır (migration gerekebilir)
 */
@Database(
    entities = [PlaybookEntity::class, StepEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PlaybookDatabase : RoomDatabase() {
    abstract fun playbookDao(): PlaybookDao
}
