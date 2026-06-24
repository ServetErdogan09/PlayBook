package com.serveterdogan.playbook.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentCourtColor by viewModel.courtColor.collectAsStateWithLifecycle()
    val currentPlayerColor by viewModel.playerColor.collectAsStateWithLifecycle()
    val currentBallColor by viewModel.ballColor.collectAsStateWithLifecycle()

    val courtColors = listOf(
        ColorItem("Koyu Mod", "#121A2A"),
        ColorItem("Klasik Parke", "#D4A373"),
        ColorItem("FIBA Mavi", "#003366"),
        ColorItem("Yeşil Saha", "#1B5E20")
    )

    val playerColors = listOf(
        ColorItem("Koyu Lacivert", "#151C2E"),
        ColorItem("Kırmızı", "#D32F2F"),
        ColorItem("Mavi", "#1976D2"),
        ColorItem("Yeşil", "#388E3C"),
        ColorItem("Beyaz", "#FFFFFF"),
        ColorItem("Siyah", "#000000")
    )

    val ballColors = listOf(
        ColorItem("Turuncu (Klasik)", "#F28C38"),
        ColorItem("FIBA Topu", "#E65100"),
        ColorItem("Sarı", "#FBC02D"),
        ColorItem("Beyaz", "#FFFFFF")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ayarlar",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Menü", tint = MaterialTheme.colorScheme.outline)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            ColorSection(
                title = "Saha Rengi",
                colors = courtColors,
                selectedColorHex = currentCourtColor,
                onColorSelected = { viewModel.updateCourtColor(it) }
            )

            ColorSection(
                title = "Oyuncu Rengi",
                colors = playerColors,
                selectedColorHex = currentPlayerColor,
                onColorSelected = { viewModel.updatePlayerColor(it) }
            )

            ColorSection(
                title = "Top Rengi",
                colors = ballColors,
                selectedColorHex = currentBallColor,
                onColorSelected = { viewModel.updateBallColor(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF151C2E).copy(alpha = 0.8f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { viewModel.resetDefault() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                        contentDescription = "Varsayılana Dön",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VARSAYILAN RENKLERE DÖN",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ColorSection(
    title: String,
    colors: List<ColorItem>,
    selectedColorHex: String,
    onColorSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            colors.forEach { item ->
                val isSelected = item.hex == selectedColorHex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(item.hex)))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(item.hex) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.name,
                        fontSize = 10.sp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

data class ColorItem(val name: String, val hex: String)
