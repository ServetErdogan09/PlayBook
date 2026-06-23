package com.serveterdogan.playbook.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun PlayBookBottomBar(navController: NavHostController) {

    val bottomNavItems = listOf(
        Routes.PlayList,
        Routes.Editor,
        Routes.AnimationViewer
    )

    NavigationBar(
        //containerColor = MaterialTheme.colorScheme.background, // Alt menü arka planı
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route){
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route){
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.background, // İkonun içi koyu
                    selectedTextColor = MaterialTheme.colorScheme.secondaryContainer, // Yazı parlak mavi
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer, // Seçili olunca arkadaki yuvarlak parlak mavi
                    unselectedIconColor = MaterialTheme.colorScheme.outline, // Seçili değilken gri
                    unselectedTextColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }



}