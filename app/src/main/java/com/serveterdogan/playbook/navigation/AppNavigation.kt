package com.serveterdogan.playbook.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.serveterdogan.playbook.ui.screens.editor.EditorScreen
import com.serveterdogan.playbook.ui.screens.playsetlist.PlaybookListScreen
import com.serveterdogan.playbook.ui.screens.viewer.AnimationViewerScreen
import com.serveterdogan.playbook.ui.theme.DrawerAccentCyan
import com.serveterdogan.playbook.ui.theme.DrawerBackground
import com.serveterdogan.playbook.ui.theme.DrawerDivider
import com.serveterdogan.playbook.ui.theme.DrawerSurface
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navItems = listOf(
        Routes.PlayList,
        Routes.Editor,
        Routes.AnimationViewer
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DrawerBackground,
                drawerTonalElevation = 0.dp,
                modifier = Modifier.width(280.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(DrawerSurface, DrawerBackground)
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Column {
                        // Logo / Uygulama Adı
                        Text(
                            text = "PLAY",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 4.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BOOK",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DrawerAccentCyan,
                                letterSpacing = 4.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            // Cyan Accent Çizgi
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(3.dp)
                                    .background(DrawerAccentCyan)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Koç Modu",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            letterSpacing = 2.sp
                        )
                    }
                }

                // Ayırıcı Çizgi
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DrawerDivider)
                )

                Spacer(Modifier.height(16.dp))

                // ─── Menü Öğeleri ──────────────────────────────────────────
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) DrawerSurface
                                else Color.Transparent
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Seçili kenarlık çizgisi
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(48.dp)
                                .background(
                                    if (isSelected) DrawerAccentCyan else Color.Transparent,
                                    RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                                )
                        )

                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    painterResource(id = item.icon),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    letterSpacing = 0.5.sp
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                val targetRoute = when (item) {
                                    is Routes.Editor -> Routes.Editor.createRoute("new_playbook")
                                    is Routes.AnimationViewer -> Routes.AnimationViewer.createRoute("new_playbook") // Veya izleyiciye boş gidiyorsa
                                    else -> item.route
                                }
                                navController.navigate(targetRoute) {
                                    // Tüm geçişlerde arka planda birikme olmaması için her zaman ana listeye kadar temizle
                                    popUpTo(Routes.PlayList.route)
                                    launchSingleTop = true
                                }
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color.Transparent,
                                selectedTextColor = DrawerAccentCyan,
                                selectedIconColor = DrawerAccentCyan,
                                unselectedContainerColor = Color.Transparent,
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                unselectedIconColor = Color.White.copy(alpha = 0.6f)
                            )
                        )
                    }
                }

                // Alt Alan Boşluk
                Spacer(Modifier.weight(1f))

                // Alt Ayırıcı ve Versiyon Bilgisi
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DrawerDivider)
                )
                Text(
                    text = "v1.0.0",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }
        }
    ) {
        // Drawer kapalıyken görünecek olan asıl ekranımız
        NavHost(
            navController = navController,
            startDestination = Routes.PlayList.route,
            modifier = Modifier
        ) {
            composable(Routes.PlayList.route) {
                PlaybookListScreen(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNavigateToEditor = { id ->
                        navController.navigate(Routes.Editor.createRoute(id))
                    },
                    onNavigateToViewer = { id ->
                        navController.navigate(Routes.AnimationViewer.createRoute(id))
                    }
                )
            }

            composable(
                route = Routes.Editor.route,
                arguments = listOf(androidx.navigation.navArgument("playbookId") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true 
                })
            ) { backStackEntry ->
                val playbookId = backStackEntry.arguments?.getString("playbookId")
                EditorScreen(
                    playbookId = playbookId,
                    onNavigateBack = { scope.launch { drawerState.open() } }
                )
            }

            composable(
                route = Routes.AnimationViewer.route,
                arguments = listOf(androidx.navigation.navArgument("playbookId") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true 
                })
            ) { backStackEntry ->
                val playbookId = backStackEntry.arguments?.getString("playbookId")
                AnimationViewerScreen(
                    playbookId = playbookId,
                    onNavigateBack = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}
