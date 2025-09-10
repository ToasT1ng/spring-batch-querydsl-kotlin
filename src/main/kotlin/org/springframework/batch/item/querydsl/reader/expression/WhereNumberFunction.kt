package org.springframework.batch.item.querydsl.reader.expression

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.NumberPath

interface WhereNumberFunction {
    fun <N> apply(id: NumberPath<N>, page: Int, currentId: N): BooleanExpression
            where N : Number, N : Comparable<*>
}