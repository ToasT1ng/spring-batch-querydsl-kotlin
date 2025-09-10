package org.springframework.batch.item.querydsl.reader

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityTransaction
import org.springframework.util.ClassUtils


class QuerydslZeroPagingItemReader<T>(
    override val entityManagerFactory: EntityManagerFactory,
    private val pageSize: Int,
    override val queryFunction: (JPAQueryFactory) -> JPAQuery<T>,
) : QuerydslPagingItemReader<T>(
    entityManagerFactory = entityManagerFactory,
    pageSize = pageSize,
    queryFunction = queryFunction
) {
    init {
        name = ClassUtils.getShortName(QuerydslZeroPagingItemReader::class.java)
        setTransacted(true)
        setPageSize(pageSize)
    }

    override fun doReadPage() {
        val tx: EntityTransaction? = getTxOrNull()

        val query = createQuery()
            .offset(0)
            .limit(pageSize.toLong())

        initResults()

        fetchQuery(query, tx)
    }
}