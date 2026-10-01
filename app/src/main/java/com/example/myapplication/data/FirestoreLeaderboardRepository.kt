package com.example.myapplication.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreLeaderboardRepository {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun fetchLeaderboard(
        onResult: (List<ScoreEntry>) -> Unit,
        onError: () -> Unit
    ) {
        db.collection("leaderboard")
            .get()
            .addOnSuccessListener { result ->
                val entries = mutableListOf<ScoreEntry>()
                for (document in result) {
                    val name = document.getString("name")?.trim() ?: "Unknown"
                    val pointsStr = document.get("points")?.toString()?.trim() ?: "0"
                    val points = pointsStr.toIntOrNull() ?: 0

                    entries.add(
                        ScoreEntry(
                            playerName = name,
                            score = points,
                            category = "Cloud",
                            mode = "Online"
                        )
                    )
                }
                onResult(entries.sortedByDescending { it.score })
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreLeaderboard", "Error fetching leaderboard", e)
                onError()
            }
    }
}
