package com.wbrawner.nanoflux.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@Entity
data class Feed(
    @PrimaryKey
    val id: Long,
    val userId: Long,
    val title: String,
    val siteUrl: String,
    val feedUrl: String,
    val checkedAt: Date,
    val etagHeader: String,
    val lastModifiedHeader: String,
    val parsingErrorMessage: String,
    val parsingErrorCount: Int,
    val scraperRules: String,
    val rewriteRules: String,
    val crawler: Boolean,
    val blocklistRules: String,
    val keeplistRules: String,
    val userAgent: String,
    val username: String,
    val password: String,
    val disabled: Boolean,
    val ignoreHttpCache: Boolean,
    val fetchViaProxy: Boolean,
    val categoryId: Long,
    val iconId: Long?
) {
    @Entity
    @JsonClass(generateAdapter = true)
    data class Icon(
        @Json(name = "feed_id")
        val feedId: Long,
        @PrimaryKey
        @Json(name = "icon_id")
        val iconId: Long
    )
}

data class FeedCategoryIcon(
    @Embedded
    val feed: Feed,
    @Relation(
        parentColumn = "id",
        entityColumn = "feedId"
    )
    val icon: Feed.Icon?,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id",
        entity = Category::class
    )
    val category: Category
)
