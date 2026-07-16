package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val company: String,
    val isPhoneContact: Boolean = false
)

@Entity(tableName = "issues")
data class Issue(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val project: String,
    val locationPath: String, // e.g. "TP/Wing - NA/F1/Flat 101/Living Room/Lights"
    val status: String, // "To Do", "Raised", "Draft"
    val description: String,
    val deadline: String,
    val category: String, // "Quality", "Safety", "Material", "Other"
    val assignee: String,
    val reporter: String,
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
