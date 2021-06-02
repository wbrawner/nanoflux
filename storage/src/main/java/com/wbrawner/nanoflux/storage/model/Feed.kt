package com.wbrawner.nanoflux.storage.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    @Serializable
    data class Icon(
        @PrimaryKey
        val id: Long,
        val data: String,
        @SerialName("mime_type")
        val mimeType: String
    )
}

data class FeedCategoryIcon(
    @Embedded
    val feed: Feed,
    @Relation(
        parentColumn = "iconId",
        entityColumn = "id",
        entity = Feed.Icon::class
    )
    val icon: Feed.Icon?,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id",
        entity = Category::class
    )
    val category: Category
)

@Serializable
data class FeedJson(
    val id: Long,
    @SerialName("user_id")
    val userId: Long,
    val title: String,
    @SerialName("site_url")
    val siteUrl: String,
    @SerialName("feed_url")
    val feedUrl: String,
    @Serializable(with = ISODateSerializer::class)
    @SerialName("checked_at")
    val checkedAt: Date,
    @SerialName("etag_header")
    val etagHeader: String,
    @SerialName("last_modified_header")
    val lastModifiedHeader: String,
    @SerialName("parsing_error_message")
    val parsingErrorMessage: String,
    @SerialName("parsing_error_count")
    val parsingErrorCount: Int,
    @SerialName("scraper_rules")
    val scraperRules: String,
    @SerialName("rewrite_rules")
    val rewriteRules: String,
    val crawler: Boolean,
    @SerialName("blocklist_rules")
    val blocklistRules: String,
    @SerialName("keeplist_rules")
    val keeplistRules: String,
    @SerialName("user_agent")
    val userAgent: String,
    val username: String,
    val password: String,
    val disabled: Boolean,
    @SerialName("ignore_http_cache")
    val ignoreHttpCache: Boolean,
    @SerialName("fetch_via_proxy")
    val fetchViaProxy: Boolean,
    val category: Category,
    @SerialName("icon")
    val icon: IconJson?
) {
    @Serializable
    data class IconJson(
        @SerialName("feed_id")
        val feedId: Long,
        @SerialName("icon_id")
        val iconId: Long
    )

    fun asFeed(): Feed = Feed(
        id,
        userId,
        title,
        siteUrl,
        feedUrl,
        checkedAt,
        etagHeader,
        lastModifiedHeader,
        parsingErrorMessage,
        parsingErrorCount,
        scraperRules,
        rewriteRules,
        crawler,
        blocklistRules,
        keeplistRules,
        userAgent,
        username,
        password,
        disabled,
        ignoreHttpCache,
        fetchViaProxy,
        category.id,
        icon?.iconId
    )
}