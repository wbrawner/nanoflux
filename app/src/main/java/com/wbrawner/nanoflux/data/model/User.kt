package com.wbrawner.nanoflux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@Entity
@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id")
    @PrimaryKey
    val id: Long,
    @Json(name = "username")
    val username: String,
    @Json(name = "is_admin")
    val admin: Boolean,
    @Json(name = "theme")
    val theme: String,
    @Json(name = "language")
    val language: String,
    @Json(name = "timezone")
    val timezone: String,
    @Json(name = "entry_sorting_direction")
    val entrySortingDirection: String,
    @Json(name = "stylesheet")
    val stylesheet: String,
    @Json(name = "google_id")
    val googleId: String,
    @Json(name = "openid_connect_id")
    val openidConnectId: String,
    @Json(name = "entries_per_page")
    val entriesPerPage: Int,
    @Json(name = "keyboard_shortcuts")
    val keyboardShortcuts: Boolean,
    @Json(name = "show_reading_time")
    val showReadingTime: Boolean,
    @Json(name = "entry_swipe")
    val entrySwipe: Boolean,
    @Json(name = "last_login_at")
    val lastLoginAt: Date,
)
