package org.springframework.batch.item.querydsl.reader.options

import jakarta.persistence.criteria.Path
import org.springframework.batch.item.querydsl.reader.expression.Expression

abstract class QuerydslNoOffsetOptions<T>(
    field: Path<*>,
    expression: Expression
) : BaseNoOffsetOptions<T>(
    fieldName = field.toString().substringAfterLast("."),
    expression = expression,
)