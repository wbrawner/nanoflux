package com.wbrawner.nanoflux.storage.dao

import androidx.room.*
import com.wbrawner.nanoflux.storage.model.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM Category WHERE id in (:ids)")
    fun getAllByIds(vararg ids: Long): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg categories: Category)

    @Delete
    fun delete(category: Category)
}