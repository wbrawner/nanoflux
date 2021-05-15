package com.wbrawner.nanoflux.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wbrawner.nanoflux.data.model.Category
import com.wbrawner.nanoflux.data.model.Feed
import com.wbrawner.nanoflux.data.model.FeedCategoryIcon

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM Category WHERE id in (:ids)")
    fun getAllByIds(vararg ids: Long): List<Category>

    @Insert
    fun insertAll(vararg categories: Category)

    @Delete
    fun delete(category: Category)
}