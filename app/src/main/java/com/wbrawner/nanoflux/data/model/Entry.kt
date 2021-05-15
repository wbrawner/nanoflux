package com.wbrawner.nanoflux.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@Entity
@JsonClass(generateAdapter = true)
data class Entry(
    @Json(name = "id")
    @PrimaryKey
    val id: Long,
    @Json(name = "user_id")
    val userId: Long,
    @Json(name = "feed_id")
    val feedId: Long,
    @Json(name = "status")
    val status: Status,
    @Json(name = "hash")
    val hash: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "url")
    val url: String,
    @Json(name = "comments_url")
    val commentsUrl: String,
    @Json(name = "published_at")
    val publishedAt: Date,
    @Json(name = "created_at")
    val createdAt: Date,
    @Json(name = "content")
    val content: String,
    @Json(name = "author")
    val author: String,
    @Json(name = "share_code")
    val shareCode: String,
    @Json(name = "starred")
    val starred: Boolean,
    @Json(name = "reading_time")
    val readingTime: Int,
) {
    enum class Status {
        READ,
        UNREAD,
        REMOVED
    }

    enum class Order {
        ID,
        STATUS,
        PUBLISHED_AT,
        CATEGORY_TITLE,
        CATEGORY_ID
    }

    enum class SortDirection {
        ASC,
        DESC
    }
}