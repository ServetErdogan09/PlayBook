package com.serveterdogan.playbook.ui.screens.viewer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serveterdogan.playbook.R
import com.serveterdogan.playbook.domain.model.Position

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationViewerScreen(
    playbookId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ViewerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(playbookId) {
        if (playbookId != null) {
            viewModel.loadPlaybook(playbookId)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = uiState.playbook?.name ?: "Set İzleyici",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (uiState.currentDescription.isNotEmpty()) {
                                Text(
                                    text = uiState.currentDescription,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Menu,
                                contentDescription = "Menü",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), thickness = 1.dp)
            }
        },
        containerColor = MaterialTheme.colorScheme.background

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Adım Göstergesi
            val totalSteps = uiState.playbook?.steps?.size ?: 0
            val displayStep = if (totalSteps == 0) 0 else (uiState.currentStepIndex + 1).coerceAtMost(totalSteps)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "Adım: $displayStep / $totalSteps",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Saha — genislige gore dolsun, aspect ratio kendi ayarlar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                TacticalBoard(
                    playerPositions = uiState.playerPositions,
                    ballPosition = uiState.ballPosition
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Playback kontrollerini Scaffold bottomBar yerine sahanın hemen altına alıyoruz
            PlaybackControls(
                isPlaying = uiState.isPlaying,
                speedMultiplier = uiState.speedMultiplier,
                onPlayClick = { viewModel.playClicked() },
                onPauseClick = { viewModel.pauseClicked() },
                onReplayClick = { viewModel.replayClicked() },
                onSpeedClick = { viewModel.toggleSpeed() }
            )
        }

    }
}

@Composable
fun TacticalBoard(
    playerPositions: Map<String, Position>,
    ballPosition: Position
) {
    val textMeasurer = rememberTextMeasurer()
    
    // Taktik tahtasının kendisi. En boy oranı yarı sahayı temsil eder (50/47)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(50f / 47f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF121A2A)) // Sahanın ahşap/taktik koyu mavi rengi
            .border(2.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // 1. SAHA ÇİZGİLERİ
            val lineColor = Color.White.copy(alpha = 0.3f)
            val strokeWidth = 2.dp.toPx()

            // Boyalı Alan (Paint) - Yarı sahanın ortasında, yukarıdan aşağı doğru
            val paintWidth = canvasWidth * 0.32f
            val paintHeight = canvasHeight * 0.45f
            val paintLeft = (canvasWidth - paintWidth) / 2f
            
            drawRect(
                color = lineColor,
                topLeft = Offset(paintLeft, 0f),
                size = Size(paintWidth, paintHeight),
                style = Stroke(width = strokeWidth)
            )

            // Serbest Atış Dairesi (Yarım daire)
            drawArc(
                color = lineColor,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(paintLeft, paintHeight - (paintWidth / 2f)),
                size = Size(paintWidth, paintWidth),
                style = Stroke(width = strokeWidth)
            )

            // 3 Sayı Çizgisi (Yay + Düz Köşeler)
            val threePointPath = Path().apply {
                val sideMargin = canvasWidth * 0.05f
                val cornerHeight = canvasHeight * 0.25f
                
                // Sol köşe düz çizgi
                moveTo(sideMargin, 0f)
                lineTo(sideMargin, cornerHeight)
                
                // Yay
                arcTo(
                    rect = Rect(
                        left = sideMargin,
                        top = -canvasHeight * 0.4f,
                        right = canvasWidth - sideMargin,
                        bottom = canvasHeight * 0.85f
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                
                // Sağ köşe düz çizgi
                lineTo(canvasWidth - sideMargin, 0f)
            }

            drawPath(
                path = threePointPath,
                color = lineColor,
                style = Stroke(width = strokeWidth)
            )

            // 2. OYUNCULARI ÇİZ
            val playerRadius = 14.dp.toPx()
            
            playerPositions.forEach { (playerId, position) ->
                val pxX = position.x * canvasWidth
                val pxY = position.y * canvasHeight
                
                // Oyuncu Yuvarlağı
                drawCircle(
                    color = Color(0xFF151C2E), // Koyu iç
                    radius = playerRadius,
                    center = Offset(pxX, pxY)
                )
                // Oyuncu Dış Çizgisi (Secondary Container Rengi)
                drawCircle(
                    color = Color(0xFF00E3FD),
                    radius = playerRadius,
                    center = Offset(pxX, pxY),
                    style = Stroke(width = 2.dp.toPx())
                )
                
                // Oyuncu ID Metni ("O1")
                val textLayoutResult = textMeasurer.measure(
                    text = playerId,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        pxX - (textLayoutResult.size.width / 2),
                        pxY - (textLayoutResult.size.height / 2)
                    )
                )
            }

            // 3. TOPU ÇİZ
            val ballPxX = ballPosition.x * canvasWidth
            val ballPxY = ballPosition.y * canvasHeight
            val ballRadius = 6.dp.toPx()
            
            drawCircle(
                color = Color(0xFFE65100), // Turuncu Basketbol Topu
                radius = ballRadius,
                center = Offset(ballPxX, ballPxY)
            )
            // Topun parlaması/çizgisi
            drawCircle(
                color = Color.Black.copy(alpha = 0.3f),
                radius = ballRadius,
                center = Offset(ballPxX, ballPxY),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    speedMultiplier: Float,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onReplayClick: () -> Unit,
    onSpeedClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // navigation barın altına girmesin
    ) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(50.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ↺ Başa Sar
            IconButton(onClick = onReplayClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Başa Sar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            // ▶ / ⏸ Büyük Play/Pause Butonu
            FloatingActionButton(
                onClick = if (isPlaying) onPauseClick else onPlayClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                    contentDescription = if (isPlaying) "Duraklat" else "Oynat",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            // Hız Butonu — her basışta 0.5x → 1x → 2x döner
            val speedLabel = when (speedMultiplier) {
                0.5f -> "0.5x"
                2.0f -> "2x"
                else -> "1x"
            }
            IconButton(onClick = onSpeedClick) {
                Text(
                    text = speedLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }  // Surface kapanış
    } // Column kapanış
}
