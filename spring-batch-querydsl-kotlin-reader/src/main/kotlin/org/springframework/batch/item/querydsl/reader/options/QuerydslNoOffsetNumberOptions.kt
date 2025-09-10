package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.impl.JPAQuery
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslNoOffsetNumberOptions<T, N>(
    private val field: NumberPath<N>,
    expression: Expression
) : QuerydslNoOffsetOptions<T>(field, expression)
        where N : Number, N : Comparable<*> {

    private var currentId: N? = null
    private var lastId: N? = null

    fun getCurrentId(): N? = currentId
    fun getLastId(): N? = lastId

    override fun initKeys(query: JPAQuery<T>, page: Int) {
        if (page == 0) {
            initFirstId(query)
            initLastId(query)
            if (logger.isDebugEnabled) {
                logger.debug("First Key= $currentId, Last Key= $lastId")
            }
        }
    }

    override fun initFirstId(query: JPAQuery<T>) {
        val clone = query.clone()
        val isGroupByQuery = isGroupByQuery(clone)

        currentId = if (isGroupByQuery) {
            clone
                .select(field)
                .orderBy(if (expression.isAsc()) field.asc() else field.desc())
                .fetchFirst()
        } else {
            clone
                .select(if (expression.isAsc()) field.min() else field.max())
                .fetchFirst()
        }
    }

    override fun initLastId(query: JPAQuery<T>) {
        val clone = query.clone()
        val isGroupByQuery = isGroupByQuery(clone)

        lastId = if (isGroupByQuery) {
            clone
                .select(field)
                .orderBy(if (expression.isAsc()) field.desc() else field.asc())
                .fetchFirst()
        } else {
            clone
                .select(if (expression.isAsc()) field.max() else field.min())
                .fetchFirst()
        }
    }

    override fun createQuery(query: JPAQuery<T>, page: Int): JPAQuery<T> {
        val currentId = this.currentId ?: return query
        return query
            .where(whereExpression(page, currentId))
            .orderBy(orderExpression())
    }

    private fun whereExpression(page: Int, currentId: N): BooleanExpression {
        val lastId = requireNotNull(this.lastId) {
            "lastId must be initialized when currentId is not null"
        }
        return expression.where(field, page, currentId)
            .and(if (expression.isAsc()) field.loe(lastId) else field.goe(lastId))
    }

    private fun orderExpression(): OrderSpecifier<N> =
        expression.order(field)

    override fun resetCurrentId(item: T) {
        @Suppress("UNCHECKED_CAST")
        currentId = getFieldValue(item) as N
        if (logger.isDebugEnabled) {
            logger.debug("Current Select Key= $currentId")
        }
    }
}