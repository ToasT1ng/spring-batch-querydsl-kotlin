package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.NumberPath
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslProjectionNoOffsetNumberOptions<T, N>(
    private val field: NumberPath<N>,
    expression: Expression,
    fieldName: String,
) : BaseNoOffsetNumberOptions<T, N>(
    field = field,
    expression = expression,
    fieldName = fieldName
) where N : Number, N : Comparable<*>