package com.serveterdogan.playbook.data.local.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.serveterdogan.playbook.data.local.entity.PlaybookEntity
import com.serveterdogan.playbook.data.local.entity.StepEntity
import com.serveterdogan.playbook.domain.model.InitialSetup
import com.serveterdogan.playbook.domain.model.Playbook
import com.serveterdogan.playbook.domain.model.Player
import com.serveterdogan.playbook.domain.model.Position
import com.serveterdogan.playbook.domain.model.Step

object PlaybookMapper {

    private val gson = Gson()

    fun toEntity(playbook: Playbook) : PlaybookEntity{
        return PlaybookEntity(
            id = playbook.id,
            name = playbook.name,
            playersJson = gson.toJson(playbook.players),
            initialSetupsJson = gson.toJson(playbook.initialSetups)
        )

    }


    fun toDomain(entity: PlaybookEntity  ,  steps : List<Step>) : Playbook{

        val playersType = object : TypeToken<List<Player>>() {}.type // runtime de ne tür bir liste götürüdğünü unutuğu için String mi player mı yoksa başka bir nesne mi unutuğu için biz bunu söyleriz tipnin ne olduğunu söyleriz
        val setupListType = object  : TypeToken<List<InitialSetup>>() {}.type
        return Playbook(
            id = entity.id,
            name = entity.name,
            players = gson.fromJson(entity.playersJson , playersType), // playersJson'ı kotlin objesine dönüştürür (List<Player>)
            initialSetups = gson.fromJson(entity.initialSetupsJson , setupListType),
            steps = steps

        )
    }


    fun toStepEntity(step : Step, playbookId: String, order: Int) : StepEntity {
        return StepEntity(
            id = step.id,
            playbookId = playbookId,
            type = step.type.name,
            primaryPlayerId = step.primaryPlayerId,
            targetPlayerId = step.targetPlayerId,
            targetPositionX = step.targetPosition?.x,
            targetPositionY = step.targetPosition?.y,
            description = step.description,
            stepOrder = order
        )
    }


    fun toStepDomain(entity : StepEntity) : Step{
        val targetPosition = if (entity.targetPositionX != null && entity.targetPositionY != null) {
            Position(entity.targetPositionX, entity.targetPositionY)
        } else {
            null
        }
        return Step(
            id = entity.id,
            type = enumValueOf(entity.type),
            primaryPlayerId = entity.primaryPlayerId,
            targetPlayerId = entity.targetPlayerId,
            targetPosition = targetPosition,
            description = entity.description
        )
    }





}