package org.springframework.batch.item.querydsl.reader.expression

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath

enum class WhereExpression(
    private val string: WhereStringFunction,
    private val number: WhereNumberFunction
) {
    GT(
        string = WhereStringFunction { id, page, currentId ->
            if (page == 0) id.goe(currentId) else id.gt(currentId)
        },
        number = object : WhereNumberFunction {
            override fun <N> apply(
                id: NumberPath<N>, page: Int, currentId: N
            ): BooleanExpression where N : Number, N : Comparable<*> =
                if (page == 0) id.goe(currentId) else id.gt(currentId)
        }
    ),
    LT(
        string = WhereStringFunction { id, page, currentId ->
            if (page == 0) id.loe(currentId) else id.lt(currentId)
        },
        number = object : WhereNumberFunction {
            override fun <N> apply(
                id: NumberPath<N>,
                page: Int,
                currentId: N
            ): BooleanExpression where N : Number, N : Comparable<*> =
                if (page == 0) id.loe(currentId) else id.lt(currentId)
        }
    );

    fun expression(id: StringPath, page: Int, currentId: String): BooleanExpression =
        string.apply(id, page, currentId)

    fun <N> expression(id: NumberPath<N>, page: Int, currentId: N): BooleanExpression
            where N : Number, N : Comparable<*> =
        number.apply(id, page, currentId)
}