package com.serveterdogan.playbook.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.serveterdogan.playbook.data.local.entity.PlaybookEntity
import com.serveterdogan.playbook.data.local.entity.StepEntity
import kotlinx.coroutines.flow.Flow

/**
 * Tüm veritabanı işlemleri (kaydet, getir, sil, güncelle) burada tanımlanır.
 * Flow kullanarak liste değişince UI otomatik güncellenir.
 */
@Dao
interface PlaybookDao {

    // ─── PLAYBOOK İŞLEMLERİ ─────────────────────────────────────

    /** Yeni bir playbook ekler veya varsa günceller */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaybook(playbook: PlaybookEntity)

    /** Tüm playbook'ları gerçek zamanlı olarak dinler */
    @Query("SELECT * FROM playbooks ORDER BY rowid DESC")
    fun getAllPlaybooks(): Flow<List<PlaybookEntity>>

    @Query("SELECT * FROM steps WHERE playbookId = :playbookId ORDER BY stepOrder ASC")
    suspend fun getStepsForPlaybookSync(playbookId: String): List<StepEntity>


    /** ID'ye göre tek bir playbook getirir */
    @Query("SELECT * FROM playbooks WHERE id = :id")
    suspend fun getPlaybookById(id: String): PlaybookEntity?

    /** Playbook'u siler (adımlar CASCADE ile otomatik silinir) */
    @Delete
    suspend fun deletePlaybook(playbook: PlaybookEntity)

    // ─── ADIM (STEP) İŞLEMLERİ ──────────────────────────────────

    /** Bir adımı ekler veya varsa günceller */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: StepEntity)

    /** Bir playbook'a ait tüm adımları sıralı getirir */
    @Query("SELECT * FROM steps WHERE playbookId = :playbookId ORDER BY stepOrder ASC")
    fun getStepsForPlaybook(playbookId: String): Flow<List<StepEntity>>

    /** Belirli bir adımı siler */
    @Query("DELETE FROM steps WHERE id = :stepId")
    suspend fun deleteStep(stepId: String)

    /** Bir playbook'un tüm adımlarını toplu ekler */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSteps(steps: List<StepEntity>)
}
