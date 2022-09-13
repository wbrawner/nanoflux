package com.wbrawner.nanoflux.storage.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.RuntimeException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Entity
@Serializable
data class Entry(
    @PrimaryKey
    val id: Long,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("feed_id")
    val feedId: Long,
    val status: Status,
    val hash: String,
    val title: String,
    val url: String,
    @SerialName("comments_url")
    val commentsUrl: String,
    @Serializable(with = ISODateSerializer::class)
    @SerialName("published_at")
    val publishedAt: Date,
    @Serializable(with = ISODateSerializer::class)
    @SerialName("created_at")
    val createdAt: Date,
    val content: String,
    val author: String,
    @SerialName("share_code")
    val shareCode: String,
    val starred: Boolean,
    @SerialName("reading_time")
    val readingTime: Int,
    val enclosures: String?
) {
    @Serializable
    enum class Status {
        @SerialName("read")
        READ,
        @SerialName("unread")
        UNREAD,
        @SerialName("removed")
        REMOVED
    }

    @Serializable
    enum class Order {
        ID,
        STATUS,
        PUBLISHED_AT,
        CATEGORY_TITLE,
        CATEGORY_ID
    }

    @Serializable
    enum class SortDirection {
        ASC,
        DESC
    }
}

data class EntryAndFeed(
    @Embedded
    val entry: Entry,
    @Relation(
        parentColumn = "feedId",
        entityColumn = "id",
        entity = Feed::class
    )
    val feed: FeedCategoryIcon,
)

object ISODateSerializer : KSerializer<Date> {
    val dateFormats = object : ThreadLocal<List<SimpleDateFormat>>() {
        override fun initialValue(): List<SimpleDateFormat> = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH),
        )
    }
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) =
        encoder.encodeString(dateFormats.get()!!.first().format(value))

    override fun deserialize(decoder: Decoder): Date {
        val dateString = decoder.decodeString()
        var exception: ParseException? = null
        dateFormats.get()!!.forEach { dateFormat ->
            try {
                return dateFormat.parse(dateString)!!
            } catch (ignored: ParseException) {
                // move onto the next format
                exception = ignored
            }
        }
        throw RuntimeException(exception)
    }
}
