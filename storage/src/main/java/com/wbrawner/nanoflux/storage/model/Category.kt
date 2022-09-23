package com.wbrawner.nanoflux.storage.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Category(
    @PrimaryKey
    val id: Long,
    val title: String,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("hide_globally")
    val hideGlobally: Boolean?
)