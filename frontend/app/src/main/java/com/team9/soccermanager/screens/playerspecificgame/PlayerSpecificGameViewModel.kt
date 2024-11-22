package com.team9.soccermanager.screens.playerspecificgame

import com.team9.soccermanager.model.accessor.Game
import com.team9.soccermanager.model.accessor.LeagueAccessor
import com.team9.soccermanager.screens.playerhome.PlayerHomeViewModel

class PlayerSpecificGameViewModel(val gameIndex: Int): PlayerHomeViewModel() {
    fun getGame(): Game {
        return LeagueAccessor.getGameFromLoaded(gameIndex)
    }
}