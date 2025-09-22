package org.springframework.batch.item.querydsl.reader.options

import org.springframework.batch.item.querydsl.reader.expression.Expression

abstract class QuerydslProjectionNoOffsetOptions<T>(
    fieldName: String,
    expression: Expression
) : BaseNoOffsetOptions<T>(
    fieldName = fieldName,
    expression = expression,
)