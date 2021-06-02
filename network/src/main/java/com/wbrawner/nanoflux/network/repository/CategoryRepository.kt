package com.wbrawner.nanoflux.network.repository

import com.wbrawner.nanoflux.network.MinifluxApiService
import com.wbrawner.nanoflux.storage.dao.CategoryDao
import com.wbrawner.nanoflux.storage.model.Category
import timber.log.Timber
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val apiService: MinifluxApiService,
    private val categoryDao: CategoryDao,
    private val logger: Timber.Tree
) {

    suspend fun getAll(fetch: Boolean = true): List<Category> {
        if (fetch) {
            categoryDao.insertAll(*apiService.getCategories().toTypedArray())
        }
        return categoryDao.getAll()
    }

    suspend fun getById(id: Long): Category? = categoryDao.getAllByIds(id).firstOrNull()
            ?: getAll(true)
            .firstOrNull { it.id == id }
}