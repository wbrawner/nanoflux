package com.wbrawner.nanoflux.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.wbrawner.nanoflux.data.dao.*
import com.wbrawner.nanoflux.data.model.Category
import com.wbrawner.nanoflux.data.model.Entry
import com.wbrawner.nanoflux.data.model.Feed
import com.wbrawner.nanoflux.data.model.User
import java.util.*

@Database(
    entities = [Feed::class, Entry::class, Category::class, Feed.Icon::class, User::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DateTypeConverter::class)
abstract class NanofluxDatabase: RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun entryDao(): EntryDao
    abstract fun categoryDao(): CategoryDao
    abstract fun iconDao(): IconDao
    abstract fun userDao(): UserDao
}

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(timestamp: Long) = Date(timestamp)

    @TypeConverter
    fun toTimestamp(date: Date) = date.time
}