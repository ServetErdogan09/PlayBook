package com.serveterdogan.playbook.ui.screens.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serveterdogan.playbook.domain.model.Position
import com.serveterdogan.playbook.domain.model.Step
import com.serveterdogan.playbook.domain.model.StepType
import com.serveterdogan.playbook.ui.theme.DrawerBackground
import com.serveterdogan.playbook.ui.theme.GlassSurface
import com.serveterdogan.playbook.ui.theme.OnSecondary
import com.serveterdogan.playbook.ui.theme.OnSecondaryContainer
import com.serveterdogan.playbook.ui.theme.SecondaryContainer
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    playbookId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showSaveSheet by remember { mutableStateOf(false) }
    val playbookName = remember { mutableStateOf(uiState.playbookName) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(playbookId) {
        if (playbookId != null && playbookId != "new_playbook") {
            viewModel.loadPlaybook(playbookId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.snackbar.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GlassSurface)
                        .border(1.dp, SecondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        color = SecondaryContainer,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Set Adı: ",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.playbookName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(start = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Menü", tint = MaterialTheme.colorScheme.outline)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val isDefaultName = uiState.playbookName.isBlank() || uiState.playbookName == "Yeni Set"
                            if (isDefaultName) {
                                // Default isim, kullanıcıdan gerçek isim iste
                                playbookName.value = ""
                                showSaveSheet = true
                            } else {
                                // Kullanıcı önceden isim vermiş, direkt kaydet
                                viewModel.savePlaybook(uiState.playbookName)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        Text("KAYDET", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                )
            )

        },
       // containerColor = MaterialTheme.colorScheme.background
        //containerColor = Color.Red
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Taktik Tahtası (Saha)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B0F1A)) // HTML'deki court container arka planı
                    .padding(16.dp)
            ) {
                EditorTacticalBoard(
                    playerPositions = uiState.playerPositions,
                    interactionState = uiState.interactionState,
                    selectedPrimaryId = uiState.selectedPrimaryPlayerId,
                    courtColorHex = uiState.courtColor,
                    playerColorHex = uiState.playerColor,
                    onPlayerDragged = { id, x, y -> viewModel.updatePlayerPosition(id, x, y) },
                    onPlayerClicked = { id -> viewModel.onPlayerClicked(id) },
                    onCourtClicked = { x, y -> viewModel.onCourtClicked(x, y) }
                )
            }

            // Glass Panel Alanı (Alt Kısım)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {

                //  Bilgi Mesajı (Hint)
                Text(
                    text = uiState.hintMessage,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )



                // 3. Aksiyon Butonları (Pas, Koşu, Perde, Şut)
                ActionButtonsRow(
                    selectedAction = uiState.selectedAction,
                    onActionSelected = { viewModel.onActionSelected(it) },
                    onCancel = { viewModel.cancelInteraction() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // yapılan hamleler sıralaması
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uiState.steps) { index, step ->
                        StepItemRow(
                            step = step,
                            index = index + 1,
                            onDelete = { viewModel.removeStep(step.id) }
                        )
                    }
                }
            }
        }


        if (showSaveSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSaveSheet = false },
                sheetState = sheetState,
                containerColor = DrawerBackground,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 8.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                ) {
                    // Başlık
                    Text(
                        text = "Seti Kaydet",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Taktiğine bir isim ver",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tema uyumlu OutlinedTextField
                    OutlinedTextField(
                        value = playbookName.value,
                        onValueChange = { playbookName.value = it },
                        label = { Text("Set Adı", color = Color.White.copy(alpha = 0.5f)) },
                        singleLine = true,
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.8f),
                            focusedBorderColor = SecondaryContainer,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            cursorColor = SecondaryContainer,
                            focusedLabelColor = SecondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Butonlar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // İptal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF151C2E))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .clickable { showSaveSheet = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "VAZGEÇ",
                                color = Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Kaydet
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (playbookName.value.isNotBlank()) SecondaryContainer
                                    else SecondaryContainer.copy(alpha = 0.3f)
                                )
                                .clickable {
                                    if (playbookName.value.isNotBlank()) {
                                        viewModel.savePlaybook(playbookName.value)
                                        showSaveSheet = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "KAYDET",
                                color = OnSecondary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditorTacticalBoard(
    playerPositions: Map<String, Position>,
    interactionState: InteractionState,
    selectedPrimaryId: String?,
    courtColorHex: String,
    playerColorHex: String,
    onPlayerDragged: (String, Float, Float) -> Unit,
    onPlayerClicked: (String) -> Unit,
    onCourtClicked: (Float, Float) -> Unit
) {
    val courtBgColor = try { Color(android.graphics.Color.parseColor(courtColorHex)) } catch (e: Exception) { Color(0xFF121A2A) }
    val playerBgColor = try { Color(android.graphics.Color.parseColor(playerColorHex)) } catch (e: Exception) { Color(0xFF151C2E) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(50f / 47f)
            .clip(RoundedCornerShape(8.dp))
            .background(courtBgColor) // Court SVG background
            .border(2.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .shadow(elevation = 15.dp, spotColor = Color(0xFF00E3FD).copy(alpha = 0.05f)) // Taktik tahtası glow
            .pointerInput(interactionState) {
                detectTapGestures { offset ->
                    val x = offset.x / size.width
                    val y = offset.y / size.height
                    onCourtClicked(x, y)
                }
            }
    ) {
        val boardWidth = constraints.maxWidth.toFloat()
        val boardHeight = constraints.maxHeight.toFloat()

        // Arka Plan Sahanın Çizgileri
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineColor = Color.White.copy(alpha = 0.2f)
            val strokeWidth = 2.dp.toPx()

            val paintWidth = size.width * 0.32f
            val paintHeight = size.height * 0.40f
            val paintLeft = (size.width - paintWidth) / 2f
            
            // pota altı boyalı alanı değiştiriyor
            drawRect(
                color = lineColor,
                topLeft = Offset(paintLeft, 0f),
                size = Size(paintWidth, paintHeight),
                style = Stroke(width = strokeWidth)
            )

            // Free throw circle
            drawArc(
                color = lineColor,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(paintLeft, paintHeight - (paintWidth / 2f)),
                size = Size(paintWidth, paintWidth),
                style = Stroke(width = strokeWidth)
            )

            // 3PT Line
            val threePointPath = Path().apply {
                val sideMargin = size.width * 0.05f
                val cornerHeight = size.height * 0.25f
                moveTo(sideMargin, 0f)
                lineTo(sideMargin, cornerHeight)
                arcTo(
                    rect = Rect(
                        left = sideMargin,
                        top = -size.height * 0.4f,
                        right = size.width - sideMargin,
                        bottom = size.height * 0.85f
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                lineTo(size.width - sideMargin, 0f)
            }
            drawPath(path = threePointPath, color = lineColor, style = Stroke(width = strokeWidth))
        }

        // Oyuncular (Sürükle bırak için Box)
        val playerRadiusDp = 16.dp // Biraz daha büyük oyuncular
        
        playerPositions.forEach { (playerId, position) ->
            val pxX = position.x * boardWidth
            val pxY = position.y * boardHeight
            val isSelected = playerId == selectedPrimaryId
            val currentPos by rememberUpdatedState(position)

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (pxX - playerRadiusDp.toPx()).roundToInt(),
                            y = (pxY - playerRadiusDp.toPx()).roundToInt()
                        )
                    }
                    .size(playerRadiusDp * 2)
                    .clickable { onPlayerClicked(playerId) }
                    .pointerInput(interactionState) {
                        if (interactionState == InteractionState.IDLE) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newPxX = (currentPos.x * boardWidth) + dragAmount.x
                                val newPxY = (currentPos.y * boardHeight) + dragAmount.y
                                onPlayerDragged(playerId, newPxX / boardWidth, newPxY / boardHeight)
                            }
                        }
                    }
                    .shadow(
                        elevation = if (isSelected) 15.dp else 5.dp,
                        shape = CircleShape,
                        spotColor = if (isSelected) Color(0xFF00E3FD) else Color.White,
                        ambientColor = if (isSelected) Color(0xFF00E3FD) else Color.White
                    )
                    // oyuncuların arka planını değiştiriyor
                    .background(
                        color = playerBgColor,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (isSelected) Color(0xFF00E3FD) else Color.White.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = playerId,
                    color = if (isSelected) Color(0xFF00E3FD) else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ActionButtonsRow(
    selectedAction: StepType?,
    onActionSelected: (StepType) -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedAction != null) {
            // İptal Butonu — temaya uygun outlined glass stil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF151C2E).copy(alpha = 0.8f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "İPTAL ET",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
        } else {
            // cardlar aksiyonlar
            StepType.values().forEach { type ->
                val title = when(type) {
                    StepType.PASS -> "PAS"
                    StepType.MOVE -> "HAREKET"
                    StepType.SCREEN -> "PERDE"
                    StepType.SHOOT -> "ŞUT"
                }

                Surface(
                    onClick = { onActionSelected(type) },
                    color = Color(0xFF151C2E).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = title, 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepItemRow(step: Step, index: Int, onDelete: () -> Unit) {
    // HTML'deki gibi aktif/inaktif görünümü
    // Step 1 görünümü: border-l-secondary-container, arka plan #151C2E
    Surface(
        color = Color(0xFF151C2E).copy(alpha = 0.9f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Sol border efekti için özel draw modifier eklenebilir ama padding ile çözelim
                .border(width = 2.dp, color = MaterialTheme.colorScheme.onPrimaryContainer, shape = RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numara Badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))

            // Açıklama
            Text(
                text = step.description.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Sil Butonu
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
