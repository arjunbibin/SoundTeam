package com.arjun.soundteam.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arjun.soundteam.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    userRole: UserRole,
    onNavigateToAttendance: () -> Unit,
    onNavigateToEquipment: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToUrgentNeeds: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome ${userRole.name}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onNavigateToAttendance,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Attendance")
        }
        
        Button(
            onClick = onNavigateToEquipment,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Equipment")
        }
        
        Button(
            onClick = onNavigateToSearch,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }
        
        Button(
            onClick = onNavigateToWishlist,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wishlist")
        }
        
        Button(
            onClick = onNavigateToUrgentNeeds,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Urgent Needs")
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
} 