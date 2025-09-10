package org.springframework.batch.item.querydsl.reader

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.batch.item.querydsl.reader.options.QuerydslNoOffsetOptions
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityTransaction
import org.springframework.util.ClassUtils
import org.springframework.util.CollectionUtils


class QuerydslNoOffsetPagingItemReader<T>(
    override val entityManagerFactory: EntityManagerFactory,
    private val pageSize: Int,
    override val queryFunction: (JPAQueryFactory) -> JPAQuery<T>,
    override var transacted: Boolean = true,
    private val options: QuerydslNoOffsetOptions<T>
) : QuerydslPagingItemReader<T>(
    entityManagerFactory = entityManagerFactory,
    pageSize = pageSize,
    queryFunction = queryFunction
) {
    init {
        name = ClassUtils.getShortName(QuerydslNoOffsetPagingItemReader::class.java)
    }

    override fun doReadPage() {
        val tx: EntityTransaction? = getTxOrNull()

        val query = createQuery()
            .limit(pageSize.toLong())

        initResults()

        fetchQuery(query, tx)

        resetCurrentIdIfNotLastPage()
    }

    override fun createQuery(): JPAQuery<T> {
        val query = queryFunction(JPAQueryFactory(entityManager))
        options.initKeys(query, page) // 제일 첫번째 페이징시 시작해야할 ID 찾기

        return options.createQuery(query, page)
    }

    private fun resetCurrentIdIfNotLastPage() {
        if (isNotEmptyResults()) {
            options.resetCurrentId(getLastItem())
        }
    }

    // 조회결과가 Empty이면 results에 null이 담긴다
    private fun isNotEmptyResults(): Boolean {
        return !CollectionUtils.isEmpty(results) && results[0] != null
    }

    private fun getLastItem(): T {
        return results[results.size - 1]
    }
}