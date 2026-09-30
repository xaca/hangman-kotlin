package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.GameMode
import com.example.myapplication.data.PlayerManager
import com.example.myapplication.data.ScoreRepository
import com.example.myapplication.data.WordRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    category: String,
    customWord: String? = null,
    onBack: () -> Unit
) {
    var currentWord by rememberSaveable(category, customWord) {
        mutableStateOf(customWord?.uppercase() ?: WordRepository.getRandomWord(category))
    }
    var guessedLetters by remember { mutableStateOf(setOf<Char>()) }
    var livesLeft by remember { mutableIntStateOf(6) }
    var hasRecordedScore by remember { mutableStateOf(false) }

    fun startNextWord() {
        guessedLetters = emptySet()
        livesLeft = 6
        hasRecordedScore = false
        currentWord = customWord?.uppercase() ?: WordRepository.getRandomWord(category)
    }

    val isWordGuessed = currentWord.isNotEmpty() && currentWord.all { it in guessedLetters }
    val isGameOver = livesLeft <= 0

    val modeText = if (PlayerManager.currentGameMode == GameMode.VS_FRIEND) "VS Friend" else "VS Computer"

    LaunchedEffect(isWordGuessed) {
        if (isWordGuessed && !hasRecordedScore) {
            hasRecordedScore = true
            val earnedScore = 100 + (livesLeft * 50) + (currentWord.length * 10)
            ScoreRepository.addScore(
                playerName = PlayerManager.playerName.ifBlank { "Player 1" },
                score = earnedScore,
                category = category,
                mode = modeText
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$category ($modeText)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Lives & Visual
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Player: ${PlayerManager.playerName.ifBlank { "Player 1" }}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lives Remaining: ${"❤️ ".repeat(livesLeft)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (livesLeft) {
                            6 -> "  +---+\n  |   |\n      |\n      |\n      |\n========="
                            5 -> "  +---+\n  |   |\n  O   |\n      |\n      |\n========="
                            4 -> "  +---+\n  |   |\n  O   |\n  |   |\n      |\n========="
                            3 -> "  +---+\n  |   |\n  O   |\n /|   |\n      |\n========="
                            2 -> "  +---+\n  |   |\n  O   |\n /|\\  |\n      |\n========="
                            1 -> "  +---+\n  |   |\n  O   |\n /|\\  |\n /    |\n========="
                            else -> "  +---+\n  |   |\n  O   |\n /|\\  |\n / \\  |\n========="
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
            }

            // Word Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentWord.forEach { char ->
                    val letterToShow = if (char in guessedLetters || isGameOver) char.toString() else "_"
                    Text(
                        text = letterToShow,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (char in guessedLetters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Game Result status or Keyboard
            if (isWordGuessed) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉 YOU WIN!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Score Added to High Scores!", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (customWord == null) {
                            Button(onClick = { startNextWord() }) {
                                Text("Play Next Word")
                            }
                        }
                        OutlinedButton(onClick = onBack) {
                            Text("Main Menu")
                        }
                    }
                }
            } else if (isGameOver) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💀 GAME OVER", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Text("The word was: $currentWord", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (customWord == null) {
                            Button(onClick = { startNextWord() }) {
                                Text("Try Another Word")
                            }
                        }
                        OutlinedButton(onClick = onBack) {
                            Text("Main Menu")
                        }
                    }
                }
            } else {
                // Letter Keyboard
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val rows = listOf("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM")
                    rows.forEach { rowLetters ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowLetters.forEach { char ->
                                val isUsed = char in guessedLetters
                                OutlinedButton(
                                    onClick = {
                                        if (!isUsed) {
                                            guessedLetters = guessedLetters + char
                                            if (char !in currentWord) {
                                                livesLeft -= 1
                                            }
                                        }
                                    },
                                    enabled = !isUsed,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = char.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
