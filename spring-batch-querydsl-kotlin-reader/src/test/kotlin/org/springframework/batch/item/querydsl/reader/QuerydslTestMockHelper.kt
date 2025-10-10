package org.springframework.batch.item.querydsl.reader

import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath
import com.querydsl.jpa.impl.JPAQuery
import io.mockk.*
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityTransaction

/**
 * Helper class for setting up common mocks in QueryDSL ItemReader tests.
 *
 * This class provides reusable mock configurations that are commonly used across
 * multiple test classes, reducing code duplication and making tests more maintainable.
 */
class QuerydslTestMockHelper {

    val entityManagerFactory: EntityManagerFactory = mockk()
    val entityManager: EntityManager = mockk(relaxed = true)
    val transaction: EntityTransaction = mockk(relaxed = true)

    /**
     * Sets up basic EntityManagerFactory and EntityManager mocks.
     * This includes transaction support, entity manager creation, and common operations.
     */
    fun setupEntityManager() {
        every { entityManagerFactory.createEntityManager(any<Map<String, Any>>()) } returns entityManager
        every { entityManager.transaction } returns transaction
        every { entityManager.close() } just Runs
        every { transaction.begin() } just Runs
        every { transaction.commit() } just Runs
        every { entityManager.flush() } just Runs
        every { entityManager.clear() } just Runs
    }

    /**
     * Creates a basic JPAQuery mock with common method chaining support.
     * Supports offset(), limit(), and fetch() methods.
     */
    inline fun <reified T> createBasicQuery(results: List<T>): JPAQuery<T> {
        val query = mockk<JPAQuery<T>>(relaxed = true)
        every { query.offset(any()) } returns query
        every { query.limit(any()) } returns query
        every { query.fetch() } returns results
        return query
    }

    /**
     * Creates a JPAQuery mock with support for multiple pages.
     * Useful for testing pagination scenarios.
     */
    inline fun <reified T> createMultiPageQuery(vararg pages: List<T>): JPAQuery<T> {
        val query = mockk<JPAQuery<T>>(relaxed = true)
        every { query.offset(any()) } returns query
        every { query.limit(any()) } returns query
        every { query.fetch() } returnsMany pages.toList()
        return query
    }

    /**
     * Creates a NumberPath mock with min/max aggregate support.
     * This is used for testing NoOffset options initialization.
     */
    inline fun <reified T> createNumberPath(): NumberPath<T> where T : Number, T : Comparable<T> {
        val numberPath = mockk<NumberPath<T>>(relaxed = true)
        every { numberPath.asc() } returns mockk()
        every { numberPath.desc() } returns mockk()
        return numberPath
    }

    /**
     * Creates a StringPath mock with common string operations.
     */
    fun createStringPath(): StringPath {
        val stringPath = mockk<StringPath>(relaxed = true)
        every { stringPath.asc() } returns mockk()
        every { stringPath.desc() } returns mockk()
        return stringPath
    }

    /**
     * Sets up a query with clone support and select type conversion.
     * This is commonly used for testing Options.initKeys() which clones the query
     * and changes the select type from entity to aggregate (min/max).
     *
     * @param entityQuery The original query for entity selection
     * @param minValue The value to return from min() aggregate
     * @param maxValue The value to return from max() aggregate
     * @param queryString The string representation of the query (for GROUP BY detection)
     */
    inline fun <reified T, reified R : Comparable<R>> setupQueryWithAggregates(
        entityQuery: JPAQuery<T>,
        minValue: R?,
        maxValue: R?,
        queryString: String = "SELECT * FROM test"
    ): JPAQuery<T> {
        val clonedQuery = mockk<JPAQuery<T>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<R>>(relaxed = true)

        every { entityQuery.clone() } returns clonedQuery
        every { clonedQuery.toString() } returns queryString
        every { selectQuery.orderBy(any()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns minValue andThen maxValue

        return clonedQuery
    }

    /**
     * Sets up aggregate functions (min/max) on a NumberPath for a cloned query.
     * This handles the type conversion from JPAQuery<T> to JPAQuery<Long>.
     */
    inline fun <reified T, reified R> setupNumberAggregates(
        clonedQuery: JPAQuery<T>,
        numberPath: NumberPath<R>,
        minValue: R?,
        maxValue: R?
    ) where R : Number, R : Comparable<R> {
        val selectQuery = mockk<JPAQuery<R>>(relaxed = true)
        every { clonedQuery.select(numberPath.min()) } returns selectQuery
        every { clonedQuery.select(numberPath.max()) } returns selectQuery
        every { clonedQuery.select(numberPath) } returns selectQuery
        every { selectQuery.orderBy(any()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns minValue andThen maxValue
    }

    /**
     * Sets up aggregate functions (min/max) on a StringPath for a cloned query.
     */
    inline fun <reified T> setupStringAggregates(
        clonedQuery: JPAQuery<T>,
        stringPath: StringPath,
        minValue: String?,
        maxValue: String?
    ) {
        val selectQuery = mockk<JPAQuery<String>>(relaxed = true)
        every { clonedQuery.select(stringPath.min()) } returns selectQuery
        every { clonedQuery.select(stringPath.max()) } returns selectQuery
        every { clonedQuery.select(stringPath) } returns selectQuery
        every { selectQuery.orderBy(any()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns minValue andThen maxValue
    }

    /**
     * Sets up a query mock with where and orderBy support for NoOffset readers.
     */
    inline fun <reified T> setupQueryWithWhereAndOrder(query: JPAQuery<T>) {
        every { query.where(any()) } returns query
        every { query.orderBy(any()) } returns query
    }

    /**
     * Sets up entity manager detach operation for non-transacted mode.
     */
    fun setupDetach() {
        every { entityManager.detach(any()) } just Runs
    }

    /**
     * Clears all mocks. Should be called in afterTest.
     */
    fun clearMocks() {
        clearAllMocks()
    }
}
