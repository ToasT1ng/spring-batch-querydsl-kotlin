package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.StringPath
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslNoOffsetStringOptions<T>(
    private val field: StringPath,
    expression: Expression
) : BaseNoOffsetStringOptions<T>(
    field = field,
    fieldName = field.toString().substringAfterLast("."),
    expression = expression,
)