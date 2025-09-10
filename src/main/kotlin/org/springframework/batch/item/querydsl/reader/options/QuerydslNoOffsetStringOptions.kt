package io.toast1ng.batchquerydslkotlin.reader.options

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.StringPath
import com.querydsl.jpa.impl.JPAQuery
import io.toast1ng.batchquerydslkotlin.reader.expression.Expression

class QuerydslNoOffsetStringOptions<T>(
    private val field: StringPath,
    expression: Expression
) : QuerydslNoOffsetOptions<T>(field, expression) {

    private var currentId: String? = null
    private var lastId: String? = null

    fun getCurrentId(): String? = currentId
    fun getLastId(): String? = lastId

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
        val isGroup = isGroupByQuery(clone)

        currentId = if (isGroup) {
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
        val isGroup = isGroupByQuery(clone)

        lastId = if (isGroup) {
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

    private fun whereExpression(page: Int, currentId: String): BooleanExpression {
        val lastId = requireNotNull(this.lastId) {
            "lastId must be initialized when currentId is not null"
        }
        return expression.where(field, page, currentId)
            .and(if (expression.isAsc()) field.loe(lastId) else field.goe(lastId))
    }

    private fun orderExpression(): OrderSpecifier<String> =
        expression.order(field)

    override fun resetCurrentId(item: T) {
        @Suppress("UNCHECKED_CAST")
        currentId = getFieldValue(item) as String
        if (logger.isDebugEnabled) {
            logger.debug("Current Select Key= $currentId")
        }
    }
}