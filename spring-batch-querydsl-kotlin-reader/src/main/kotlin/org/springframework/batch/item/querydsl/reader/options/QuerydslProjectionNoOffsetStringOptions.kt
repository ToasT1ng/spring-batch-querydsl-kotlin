package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.StringPath
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslProjectionNoOffsetStringOptions<T>(
    private val field: StringPath,
    fieldName: String,
    expression: Expression,
) : BaseNoOffsetStringOptions<T>(
    field = field,
    fieldName = fieldName,
    expression = expression,
)