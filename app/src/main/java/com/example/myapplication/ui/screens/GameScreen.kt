package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import com.example.myapplication.data.FirestoreCloudWordRepository
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.ui.tooling.preview.Preview
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    category: String,
    customWord: String? = null,
    onBack: () -> Unit
) {
    var cloudHint by rememberSaveable { mutableStateOf<String?>(null) }
    var cloudError by rememberSaveable { mutableStateOf(false) }
    var currentWord by rememberSaveable(category, customWord) {
        mutableStateOf(customWord?.uppercase() ?: if (category == "Cloud") "LOADING..." else WordRepository.getRandomWord(category))
    }
    var guessedLetters by remember { mutableStateOf(setOf<Char>()) }
    var livesLeft by remember { mutableIntStateOf(6) }
    var hasRecordedScore by remember { mutableStateOf(false) }

    LaunchedEffect(category) {
        if (category == "Cloud" && customWord == null) {
            cloudError = false
            FirestoreCloudWordRepository.fetchRandomCloudWord(
                onResult = { cloudWord ->
                    if (cloudWord != null) {
                        currentWord = cloudWord.word
                        cloudHint = cloudWord.category
                        cloudError = false
                    } else {
                        cloudError = true
                    }
                },
                onError = {
                    cloudError = true
                }
            )
        }
    }

    fun startNextWord() {
        guessedLetters = emptySet()
        livesLeft = 6
        hasRecordedScore = false
        if (category == "Cloud") {
            currentWord = "LOADING..."
            cloudError = false
            FirestoreCloudWordRepository.fetchRandomCloudWord(
                onResult = { cloudWord ->
                    if (cloudWord != null) {
                        currentWord = cloudWord.word
                        cloudHint = cloudWord.category
                        cloudError = false
                    } else {
                        cloudError = true
                    }
                },
                onError = {
                    cloudError = true
                }
            )
        } else {
            currentWord = customWord?.uppercase() ?: WordRepository.getRandomWord(category)
        }
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (cloudError) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️ No internet connection or cloud words found in Firestore.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = onBack) {
                                Text("Go Back")
                            }
                        }
                    }
                } else if (cloudHint != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "💡 Hint Category: $cloudHint",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

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
                    val mistakes = (6 - livesLeft).coerceIn(0, 6)
                    SpriteSheetView(
                        drawableResId = R.drawable.pasos_horca,
                        frameIndex = mistakes,
                        totalFrames = 7,
                        modifier = Modifier.height(160.dp)
                    )
                }
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

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    MyApplicationTheme {
        GameScreen(
            category = "Animals",
            onBack = {}
        )
    }
}

@Composable
fun SpriteSheetView(
    drawableResId: Int,
    frameIndex: Int,
    totalFrames: Int = 7,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember(drawableResId) {
        BitmapFactory.decodeResource(context.resources, drawableResId)
    }

    val frameWidth = remember(bitmap) { if (bitmap != null) bitmap.width / totalFrames else 1 }
    val frameHeight = remember(bitmap) { if (bitmap != null) bitmap.height else 1 }
    val aspectRatio = if (frameHeight > 0) frameWidth.toFloat() / frameHeight.toFloat() else 1f

    Canvas(modifier = modifier.aspectRatio(aspectRatio)) {
        if (bitmap != null) {
            val currentFrame = frameIndex.coerceIn(0, totalFrames - 1)
            val srcX = currentFrame * frameWidth

            val srcRect = Rect(srcX, 0, srcX + frameWidth, frameHeight)
            val dstRect = RectF(0f, 0f, size.width, size.height)

            drawContext.canvas.nativeCanvas.drawBitmap(bitmap, srcRect, dstRect, null)
        }
    }
}
