package com.example.myapplication.data

import androidx.compose.runtime.mutableStateListOf

data class ScoreEntry(
    val playerName: String,
    val score: Int,
    val category: String,
    val mode: String = "VS Computer"
)

object ScoreRepository {
    private val _scores = mutableStateListOf(
        ScoreEntry("Alex", 1250, "Animals"),
        ScoreEntry("Maria", 980, "Movies & Shows"),
        ScoreEntry("Chris", 820, "Technology"),
        ScoreEntry("Jordan", 650, "Countries"),
        ScoreEntry("Taylor", 400, "Sports")
    )

    val scores: List<ScoreEntry> get() = _scores.sortedByDescending { it.score }

    fun addScore(playerName: String, score: Int, category: String, mode: String = "VS Computer") {
        _scores.add(ScoreEntry(playerName, score, category, mode))
    }
}
