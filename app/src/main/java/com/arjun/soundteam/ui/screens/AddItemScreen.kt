package com.arjun.soundteam.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arjun.soundteam.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onItemAdded: (Item) -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ItemCategory.EQUIPMENT) }
    var selectedCondition by remember { mutableStateOf(ItemCondition.GOOD) }
    var quantity by remember { mutableStateOf("1") }
    var needsService by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var conditionExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Text(
            text = "Add New Item",
            style = MaterialTheme.typography.headlineMedium
        )

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory.displayName(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text("Category") }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ItemCategory.values().forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName()) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        // Item Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Condition Dropdown
        ExposedDropdownMenuBox(
            expanded = conditionExpanded,
            onExpandedChange = { conditionExpanded = !conditionExpanded }
        ) {
            OutlinedTextField(
                value = selectedCondition.displayName(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text("Condition") }
            )
            ExposedDropdownMenu(
                expanded = conditionExpanded,
                onDismissRequest = { conditionExpanded = false }
            ) {
                ItemCondition.values().forEach { condition ->
                    DropdownMenuItem(
                        text = { Text(condition.displayName()) },
                        onClick = {
                            selectedCondition = condition
                            conditionExpanded = false
                        }
                    )
                }
            }
        }

        // Quantity
        OutlinedTextField(
            value = quantity,
            onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) quantity = it },
            label = { Text("Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Service Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Needs Service")
            Switch(
                checked = needsService,
                onCheckedChange = { needsService = it }
            )
        }

        // Add Button
        Button(
            onClick = {
                if (name.isNotBlank() && quantity.toIntOrNull() != null) {
                    val item = Item(
                        name = name,
                        category = selectedCategory,
                        condition = selectedCondition,
                        quantity = quantity.toInt(),
                        needsService = needsService
                    )
                    onItemAdded(item)
                    onNavigateBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && quantity.toIntOrNull() != null
        ) {
            Text("Add Item")
        }
    }
} 