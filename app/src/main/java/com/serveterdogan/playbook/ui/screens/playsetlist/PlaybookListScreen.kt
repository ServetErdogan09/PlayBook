package com.serveterdogan.playbook.ui.screens.playsetlist

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serveterdogan.playbook.domain.model.Playbook
import com.serveterdogan.playbook.ui.theme.Background
import com.serveterdogan.playbook.ui.theme.Error
import com.serveterdogan.playbook.ui.theme.GlassSurface
import com.serveterdogan.playbook.ui.theme.OnSecondaryContainer
import com.serveterdogan.playbook.ui.theme.OnSurface
import com.serveterdogan.playbook.ui.theme.OnSurfaceVariant
import com.serveterdogan.playbook.ui.theme.OutlineVariant
import com.serveterdogan.playbook.ui.theme.Primary
import com.serveterdogan.playbook.ui.theme.PrimaryContainer
import com.serveterdogan.playbook.ui.theme.SecondaryContainer
import com.serveterdogan.playbook.ui.theme.SurfaceContainer
import com.serveterdogan.playbook.ui.theme.SurfaceContainerHigh
import com.serveterdogan.playbook.ui.theme.Tertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybookListScreen(
    onMenuClick: () -> Unit,
    onNavigateToEditor: (String) -> Unit = {},
    onNavigateToViewer: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PlaybookListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Playbook", fontWeight = FontWeight.Bold, color = Primary, fontSize = 24.sp) 
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menü", tint = Primary)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .background(SurfaceContainerHigh, CircleShape)
                            .border(1.dp, OutlineVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profil", tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background.copy(alpha = 0.8f),
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToEditor("new_playbook") },
                containerColor = SecondaryContainer,
                contentColor = OnSecondaryContainer,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 32.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yeni Ekle", modifier = Modifier.size(32.dp))
            }
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Hücum Setleriniz",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OnSurface,
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kayıtlı taktikleriniz ve oyun planlarınız.",
                fontSize = 16.sp,
                color = OnSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is PlayBookListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is PlayBookListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(text = "Hata oluştu!", color = Error, fontSize = 18.sp)
                    }
                }
                is PlayBookListUiState.Success -> {
                    if (state.playlists.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = OnSurfaceVariant.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Playbook Boş",
                                color = OnSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Henüz taktik setiniz yok.\nSağ alttan ekleyebilirsiniz.",
                                color = OnSurfaceVariant,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 300.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(state.playlists) { playbook ->
                                PlaybookCard(
                                    playbook = playbook,
                                    onClick = { onNavigateToEditor(playbook.id) },
                                    onPlayClick = { onNavigateToViewer(playbook.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaybookCard(
    playbook: Playbook,
    onClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GlassSurface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Tactic Board Preview from Initial Setups
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0B0F1A))
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Oyuncuların başlangıç pozisyonlarını çiz
                    playbook.initialSetups.forEach { setup ->
                        val x = size.width * setup.position.x
                        val y = size.height * setup.position.y
                        drawCircle(
                            color = PrimaryContainer,
                            radius = 12f,
                            center = Offset(x, y)
                        )
                    }
                }
            }

            // Bottom Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = playbook.name,
                        color = OnSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Log.d("PlayBookListScreen","playbook : $playbook - steps : ${playbook.steps.size}")
                        val playerTag = "${playbook.players.size} Oyuncu"
                        val stepTag = "${playbook.steps.size} Adım"
                        TagChip(playerTag, Primary)
                        TagChip(stepTag, Tertiary)
                    }
                }
                
                // Play Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainer.copy(alpha = 0.2f))
                        .border(1.dp, SecondaryContainer, CircleShape)
                        .clickable { onPlayClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Oynat", tint = SecondaryContainer)
                }
            }
        }
    }
}

@Composable
fun TagChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceContainer)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}