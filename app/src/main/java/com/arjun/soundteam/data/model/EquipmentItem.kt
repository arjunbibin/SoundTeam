package com.arjun.soundteam.data.model

data class EquipmentItem(
    val id: String = "",
    val name: String = "",
    val category: ItemCategory = ItemCategory.EQUIPMENT,
    val condition: ItemCondition = ItemCondition.GOOD,
    val count: Int = 1,
    val length: Double = 0.0 // Default 0 for non-cable items
    val needsService: Boolean = false
)
