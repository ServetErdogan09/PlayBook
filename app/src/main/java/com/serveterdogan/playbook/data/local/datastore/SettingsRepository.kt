package com.serveterdogan.playbook.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val COURT_COLOR = stringPreferencesKey("court_color")
        val PLAYER_COLOR = stringPreferencesKey("player_color")
        val BALL_COLOR = stringPreferencesKey("ball_color")

        const val DEFAULT_COURT_COLOR = "#121A2A"
        const val DEFAULT_PLAYER_COLOR = "#151C2E"
        const val DEFAULT_BALL_COLOR = "#F28C38"
    }

    val courtColor: Flow<String> = dataStore.data.map { preferences ->
        preferences[COURT_COLOR] ?: DEFAULT_COURT_COLOR
    }

    val playerColor: Flow<String> = dataStore.data.map { preferences ->
        preferences[PLAYER_COLOR] ?: DEFAULT_PLAYER_COLOR
    }

    val ballColor: Flow<String> = dataStore.data.map { preferences ->
        preferences[BALL_COLOR] ?: DEFAULT_BALL_COLOR
    }

    suspend fun setCourtColor(colorHex: String) {
        dataStore.edit { preferences ->
            preferences[COURT_COLOR] = colorHex
        }
    }

    suspend fun setPlayerColor(colorHex: String) {
        dataStore.edit { preferences ->
            preferences[PLAYER_COLOR] = colorHex
        }
    }

    suspend fun setBallColor(colorHex: String) {
        dataStore.edit { preferences ->
            preferences[BALL_COLOR] = colorHex
        }
    }


    suspend fun resetDefaultColor(){
        dataStore.edit { preferences->
            preferences[COURT_COLOR] = DEFAULT_COURT_COLOR
            preferences[PLAYER_COLOR] = DEFAULT_PLAYER_COLOR
            preferences[BALL_COLOR] = DEFAULT_BALL_COLOR
        }
    }
}
