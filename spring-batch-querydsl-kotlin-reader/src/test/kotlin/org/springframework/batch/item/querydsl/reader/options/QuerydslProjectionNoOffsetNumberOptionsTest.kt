package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.NumberPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslProjectionNoOffsetNumberOptionsTest : FunSpec({

    test("should use provided fieldName for projections") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "sum(testEntity.amount)"

        val options = QuerydslProjectionNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            fieldName = "totalAmount",
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        // Should use provided fieldName, not extract from path
        fieldName shouldBe "totalAmount"
    }

    test("should work with projection queries") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "count(testEntity.id)"

        val options = QuerydslProjectionNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            fieldName = "count",
            expression = Expression.DESC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "count"
    }

    test("should properly initialize with NumberPath and fieldName") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "avg(testEntity.rating)"

        val options = QuerydslProjectionNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            fieldName = "averageRating",
            expression = Expression.ASC
        )

        val expressionField = options::class.java.superclass.superclass.getDeclaredField("expression")
        expressionField.isAccessible = true
        val expression = expressionField.get(options) as Expression

        expression shouldBe Expression.ASC
    }

    test("should support different number types - Long") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "sum(amount)"

        val options = QuerydslProjectionNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            fieldName = "sumAmount",
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "sumAmount"
    }

    test("should support different number types - Integer") {
        val numberPath = mockk<NumberPath<Int>>()
        every { numberPath.toString() } returns "count(*)"

        val options = QuerydslProjectionNoOffsetNumberOptions<Any, Int>(
            field = numberPath,
            fieldName = "totalCount",
            expression = Expression.DESC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "totalCount"
    }

    test("should support different number types - Double") {
        val numberPath = mockk<NumberPath<Double>>()
        every { numberPath.toString() } returns "avg(price)"

        val options = QuerydslProjectionNoOffsetNumberOptions<Any, Double>(
            field = numberPath,
            fieldName = "avgPrice",
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "avgPrice"
    }

    test("should work with complex projection expressions") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "sum(testEntity.quantity * testEntity.price)"

        val options = QuerydslProjectionNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            fieldName = "totalValue",
            expression = Expression.DESC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "totalValue"
    }

    test("should handle simple field name for projections") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "max(id)"

        val options = QuerydslProjectionNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            fieldName = "maxId",
            expression = Expression.DESC
        )

        val expressionField = options::class.java.superclass.superclass.getDeclaredField("expression")
        expressionField.isAccessible = true
        val expression = expressionField.get(options) as Expression

        expression shouldBe Expression.DESC
    }
})
