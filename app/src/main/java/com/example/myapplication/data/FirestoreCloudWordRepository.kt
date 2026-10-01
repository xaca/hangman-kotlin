package com.example.myapplication.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

data class CloudWord(
    val word: String,
    val category: String
)

object FirestoreCloudWordRepository {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun fetchRandomCloudWord(
        onResult: (CloudWord?) -> Unit,
        onError: () -> Unit
    ) {
        db.collection("cloud_words")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val documents = result.documents
                    val randomDoc = documents.random()
                    
                    val rawWord = randomDoc.getString("word")?.trim() ?: ""
                    val rawCategory = randomDoc.getString("category")?.trim() ?: "Cloud"

                    if (rawWord.isNotEmpty()) {
                        val normalizedWord = rawWord.uppercase()
                        val normalizedCategory = rawCategory.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        onResult(CloudWord(normalizedWord, normalizedCategory))
                    } else {
                        onError()
                    }
                } else {
                    onError()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreCloudWord", "Error fetching cloud word", e)
                onError()
            }
    }
}
