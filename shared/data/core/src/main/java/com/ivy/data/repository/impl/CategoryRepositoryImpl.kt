package com.ivy.data.repository.impl

import com.ivy.base.di.AppCoroutineScope
import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.DataObserver
import com.ivy.data.DataWriteEvent
import com.ivy.data.DeleteOperation
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.write.WriteCategoryDao
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.mapper.CategoryMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val mapper: CategoryMapper,
    private val writeCategoryDao: WriteCategoryDao,
    private val categoryDao: CategoryDao,
    private val dispatchersProvider: DispatchersProvider,
    private val dataObserver: DataObserver,
    @AppCoroutineScope
    private val appCoroutineScope: CoroutineScope
) : CategoryRepository {

    init {
        appCoroutineScope.launch {
            dataObserver.writeEvents.collectLatest { event ->
                when (event) {
                    DataWriteEvent.AllDataChange -> categoriesMemo.clear()
                    is DataWriteEvent.DeleteCategories -> {
                        when (val op = event.operation) {
                            DeleteOperation.All -> categoriesMemo.clear()
                            is DeleteOperation.Just -> {
                                op.ids.forEach(categoriesMemo::remove)
                            }
                        }
                    }

                    is DataWriteEvent.SaveCategories -> {
                        event.categories.map(Category::id)
                            .forEach(categoriesMemo::remove)
                    }

                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    private val categoriesMemo = mutableMapOf<CategoryId, Category>()
    private var findAllMemoized: Boolean = false

    override suspend fun findAll(deleted: Boolean): List<Category> {
        return if (findAllMemoized) {
            categoriesMemo.values.sortedBy { it.orderNum }
        } else {
            withContext(dispatchersProvider.io) {
                categoryDao.findAll(deleted).mapNotNull {
                    with(mapper) { it.toDomain() }.getOrNull()
                }.also(::memoize).also {
                    findAllMemoized = true
                }
            }
        }
    }

    override suspend fun findById(id: CategoryId): Category? {
        return categoriesMemo[id] ?: withContext(dispatchersProvider.io) {
            categoryDao.findById(id.value)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }.also {
                if (it != null) {
                    categoriesMemo[id] = it
                }
            }
        }
    }

    override suspend fun findMaxOrderNum(): Double {
        return if (categoriesMemo.isNotEmpty()) {
            categoriesMemo.maxOfOrNull { (_, category) -> category.orderNum } ?: 0.0
        } else {
            withContext(dispatchersProvider.io) {
                categoryDao.findMaxOrderNum() ?: 0.0
            }
        }
    }

    override suspend fun save(value: Category) {
        return withContext(dispatchersProvider.io) {
            writeCategoryDao.save(
                with(mapper) { value.toEntity() }
            )
            categoriesMemo[value.id] = value
            dataObserver.post(DataWriteEvent.SaveCategories(listOf(value)))
        }
    }

    override suspend fun saveMany(values: List<Category>) {
        withContext(dispatchersProvider.io) {
            writeCategoryDao.saveMany(
                values.map { with(mapper) { it.toEntity() } }
            )
            memoize(values)
            dataObserver.post(DataWriteEvent.SaveCategories(values))
        }
    }

    override suspend fun deleteById(id: CategoryId) {
        withContext(dispatchersProvider.io) {
            categoriesMemo.remove(id)
            writeCategoryDao.deleteById(id.value)
            dataObserver.post(
                DataWriteEvent.DeleteCategories(
                    DeleteOperation.Just(listOf(id))
                )
            )
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchersProvider.io) {
            categoriesMemo.clear()
            writeCategoryDao.deleteAll()
            dataObserver.post(DataWriteEvent.DeleteCategories(DeleteOperation.All))
        }
    }

    private fun memoize(accounts: List<Category>) {
        accounts.forEach {
            categoriesMemo[it.id] = it
        }
    }
}
