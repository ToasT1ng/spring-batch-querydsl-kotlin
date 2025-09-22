package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.StringPath
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslProjectionNoOffsetStringOptions<T>(
    private val field: StringPath,
    expression: Expression,
    inputFieldName: String,
) : BaseNoOffsetStringOptions<T>(
    field = field,
    expression = expression,
    fieldName = inputFieldName
)