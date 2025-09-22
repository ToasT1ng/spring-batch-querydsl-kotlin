package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.NumberPath
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslProjectionNoOffsetNumberOptions<T, N>(
    private val field: NumberPath<N>,
    fieldName: String,
    expression: Expression,
) : BaseNoOffsetNumberOptions<T, N>(
    field = field,
    fieldName = fieldName,
    expression = expression,
) where N : Number, N : Comparable<*>