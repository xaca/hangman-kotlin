package com.example.myapplication.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class GameMode {
    VS_COMPUTER,
    VS_FRIEND
}

object PlayerManager {
    var playerName by mutableStateOf("")
    var currentGameMode by mutableStateOf(GameMode.VS_COMPUTER)
}
