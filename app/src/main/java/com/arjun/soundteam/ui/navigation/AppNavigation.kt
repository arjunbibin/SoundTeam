package com.arjun.soundteam.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arjun.soundteam.ui.screens.*
import com.arjun.soundteam.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,  // Pass from MainActivity
    viewModel: MainViewModel,
    startDestination: String = "login"
) {
    Scaffold(
        topBar = {
            if (navController.previousBackStackEntry != null) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("main") {
                MainScreen(
                    viewModel = viewModel,
                    onNavigateToEquipment = { navController.navigate("equipment") },
                    onNavigateToAttendance = { navController.navigate("attendance") },
                    onNavigateToSearch = { navController.navigate("search") }
                )
            }
            composable("attendance") {
                AttendanceScreen(viewModel = viewModel)
            }
            composable("equipment") {
                AddEquipmentScreen(
                    viewModel = viewModel,
                    onBackPressed = { navController.navigateUp() }
                )
            }
            composable("detailed_attendance/{memberId}") { backStackEntry ->
                DetailedAttendanceScreen(
                    teamMemberName = backStackEntry.arguments?.getString("memberId") ?: "",
                    attendanceData = viewModel.attendanceData.value,
                    onBackPressed = { navController.navigateUp() }
                )
            }
        }
    }
}
