package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.NumberPath
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslNoOffsetNumberOptions<T, N>(
    private val field: NumberPath<N>,
    expression: Expression
) : BaseNoOffsetNumberOptions<T, N>(
    field = field,
    fieldName = field.toString().substringAfterLast("."),
    expression = expression,
) where N : Number, N : Comparable<*>