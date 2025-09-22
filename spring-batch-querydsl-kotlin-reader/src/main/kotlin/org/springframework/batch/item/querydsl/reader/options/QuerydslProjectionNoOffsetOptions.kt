package org.springframework.batch.item.querydsl.reader.options

import org.springframework.batch.item.querydsl.reader.expression.Expression

abstract class QuerydslProjectionNoOffsetOptions<T>(
    inputFieldName: String,
    expression: Expression
) : BaseNoOffsetOptions<T>(
    expression = expression,
    fieldName = inputFieldName
)