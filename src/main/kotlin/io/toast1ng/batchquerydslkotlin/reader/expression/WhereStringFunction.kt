package io.toast1ng.batchquerydslkotlin.reader.expression

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.StringPath

fun interface WhereStringFunction {
    fun apply(id: StringPath, page: Int, currentId: String): BooleanExpression
}