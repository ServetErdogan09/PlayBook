package com.serveterdogan.playbook.data.repo

import android.util.Log
import com.serveterdogan.playbook.data.local.PlaybookDao
import com.serveterdogan.playbook.data.local.mapper.PlaybookMapper
import com.serveterdogan.playbook.domain.model.Playbook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// hillt ile bunu dışardan sağlayacağız
class PlaybookRepository @Inject constructor(
    private val dao: PlaybookDao
) {


    // tüm playbook listesini çek
    fun getAllPlayBook() : Flow<List<Playbook>>{
         return  dao.getAllPlaybooks().map { entities->
             entities.map { playbookEntity ->
                 val stepEntities = dao.getStepsForPlaybookSync(playbookEntity.id)
                 val steps = stepEntities.map { PlaybookMapper.toStepDomain(it) }
                 PlaybookMapper.toDomain(playbookEntity, steps)
             }
         }
    }


    // Detay sayfasına (Editöre veya İzleyiciye) girildiğinde çalışacak
    suspend fun getPlaybookWithSteps(id: String): Playbook? {
        val entity = dao.getPlaybookById(id) ?: return null

        val stepEntities = dao.getStepsForPlaybookSync(id)

        val steps = stepEntities.map { PlaybookMapper.toStepDomain(it) }
        return PlaybookMapper.toDomain(entity, steps)
    }



    // playbook ekle
    suspend fun savePlaybook(playbook: Playbook){
       val entity = PlaybookMapper.toEntity(playbook = playbook)
        dao.insertPlaybook(entity)

        val stepEntities = playbook.steps.mapIndexed { index , step ->
            Log.d("PlaybookRepository", "Step: ${playbook.steps.size} , index: $index")
            PlaybookMapper.toStepEntity(order = index , playbookId = playbook.id , step = step)
        }

        dao.insertAllSteps(stepEntities)
    }


    // Seti silmek için
    suspend fun deletePlaybook(playbook: Playbook) {
        val entity = PlaybookMapper.toEntity(playbook)
        dao.deletePlaybook(entity)
    }






}