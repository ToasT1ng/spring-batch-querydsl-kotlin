package org.springframework.batch.item.querydsl.reader

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityTransaction
import org.springframework.batch.item.database.AbstractPagingItemReader
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.util.ClassUtils
import org.springframework.util.CollectionUtils
import java.util.concurrent.CopyOnWriteArrayList

open class QuerydslPagingItemReader<T>(
    protected open val entityManagerFactory: EntityManagerFactory,
    pageSize: Int,
    protected open val queryFunction: (JPAQueryFactory) -> JPAQuery<T>,
    protected open var transacted: Boolean = true
) : AbstractPagingItemReader<T>() {

    protected lateinit var entityManager: EntityManager
    protected val jpaPropertyMap = mutableMapOf<String, Any>()


    init {
        name = ClassUtils.getShortName(QuerydslPagingItemReader::class.java)
        setPageSize(pageSize)
    }

    fun setTransacted(transacted: Boolean) {
        this.transacted = transacted
    }

    override fun doOpen() {
        super.doOpen()

        entityManager = entityManagerFactory.createEntityManager(jpaPropertyMap)
            ?: throw DataAccessResourceFailureException("Unable to obtain an EntityManager")
    }

    override fun doReadPage() {
        val tx: EntityTransaction? = getTxOrNull()

        val query = createQuery()
            .offset((page * pageSize).toLong())
            .limit(pageSize.toLong())

        initResults()
        fetchQuery(query, tx)
    }

    protected fun getTxOrNull(): EntityTransaction? {
        if (transacted) {
            val tx = entityManager.transaction
            tx.begin()

            entityManager.flush()
            entityManager.clear()
            return tx
        }

        return null
    }

    protected open fun createQuery() =
        queryFunction(JPAQueryFactory(entityManager))


    protected fun initResults() {
        if (CollectionUtils.isEmpty(results)) {
            results = CopyOnWriteArrayList()
        } else {
            results.clear()
        }
    }

    protected fun fetchQuery(query: JPAQuery<T>, tx: EntityTransaction?) {
        if (transacted) {
            results.addAll(query.fetch())
            tx?.commit()
        } else {
            val queryResult = query.fetch()
            for (entity in queryResult) {
                entityManager.detach(entity)
                results.add(entity)
            }
        }
    }

    override fun jumpToItem(itemIndex: Int) {

    }

    override fun doClose() {
        entityManager.close()
        super.doClose()
    }
}