package com.wbrawner.nanoflux.storage.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Entity
@Serializable
data class User(
    @PrimaryKey
    val id: Long,
    val username: String,
    @SerialName("is_admin")
    val admin: Boolean,
    val theme: String,
    val language: String,
    val timezone: String,
    @SerialName("entry_sorting_direction")
    val entrySortingDirection: String,
    val stylesheet: String,
    @SerialName("google_id")
    val googleId: String,
    @SerialName("openid_connect_id")
    val openidConnectId: String,
    @SerialName("entries_per_page")
    val entriesPerPage: Int,
    @SerialName("keyboard_shortcuts")
    val keyboardShortcuts: Boolean,
    @SerialName("show_reading_time")
    val showReadingTime: Boolean,
    @SerialName("entry_swipe")
    val entrySwipe: Boolean,
    @Serializable(with = ISODateSerializer::class)
    @SerialName("last_login_at")
    val lastLoginAt: Date,
    @SerialName("display_mode")
    val displayMode: String
)
