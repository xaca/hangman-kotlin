package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.FirestoreCloudWordRepository
import com.example.myapplication.data.NetworkUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(
    onCategorySelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val isConnected = remember { NetworkUtils.isNetworkAvailable(context) }
    val categories = remember(isConnected) {
        val list = mutableListOf(
            "🐶 Animals",
            "🎬 Movies & Shows",
            "⚽ Sports",
            "🌍 Countries",
            "💻 Technology",
            "🍔 Food & Drinks"
        )
        if (isConnected) {
            list.add(0, "☁️ Cloud")
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Word Category") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Choose a category to start playing:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        Card(
                            onClick = {
                                val cleanCategory = category.substringAfter(" ")
                                if (cleanCategory == "Cloud") {
                                    isLoading = true
                                    FirestoreCloudWordRepository.fetchRandomCloudWord(
                                        onResult = { cloudWord ->
                                            isLoading = false
                                            if (cloudWord != null) {
                                                Toast.makeText(
                                                    context,
                                                    "Cloud Word: ${cloudWord.word} (${cloudWord.category})",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "No cloud words found",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            onCategorySelected(cleanCategory)
                                        },
                                        onError = {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Error fetching from Firebase",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onCategorySelected(cleanCategory)
                                        }
                                    )
                                } else {
                                    onCategorySelected(cleanCategory)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = category,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                }
            }

            if (!isConnected) {
                Text(
                    text = "Offline Mode",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
