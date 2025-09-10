package org.springframework.batch.item.querydsl.reader.expression

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath

enum class Expression(
    val where: WhereExpression,
    val order: OrderExpression
) {
    ASC(WhereExpression.GT, OrderExpression.ASC),
    DESC(WhereExpression.LT, OrderExpression.DESC);

    fun isAsc(): Boolean {
        return this === ASC
    }

    fun where(id: StringPath, page: Int, currentId: String): BooleanExpression {
        return where.expression(id, page, currentId)
    }

    fun <N> where(
        id: NumberPath<N>,
        page: Int,
        currentId: N
    ): BooleanExpression where N : Number, N : Comparable<*> {
        return where.expression(id, page, currentId)
    }

    fun order(id: StringPath): OrderSpecifier<String> {
        return if (isAsc()) id.asc() else id.desc()
    }

    fun <N> order(id: NumberPath<N>): OrderSpecifier<N> where N : Number, N : Comparable<*> {
        return if (isAsc()) id.asc() else id.desc()
    }
}

