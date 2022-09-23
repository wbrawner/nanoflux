package com.wbrawner.nanoflux.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

@Database(
    entities = [com.wbrawner.nanoflux.storage.model.Feed::class, com.wbrawner.nanoflux.storage.model.Entry::class, com.wbrawner.nanoflux.storage.model.Category::class, com.wbrawner.nanoflux.storage.model.Feed.Icon::class, com.wbrawner.nanoflux.storage.model.User::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(DateTypeConverter::class)
abstract class NanofluxDatabase: RoomDatabase() {
    abstract fun feedDao(): com.wbrawner.nanoflux.storage.dao.FeedDao
    abstract fun entryDao(): com.wbrawner.nanoflux.storage.dao.EntryDao
    abstract fun categoryDao(): com.wbrawner.nanoflux.storage.dao.CategoryDao
    abstract fun iconDao(): com.wbrawner.nanoflux.storage.dao.IconDao
    abstract fun userDao(): com.wbrawner.nanoflux.storage.dao.UserDao
}

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(timestamp: Long) = Date(timestamp)

    @TypeConverter
    fun toTimestamp(date: Date) = date.time
}