package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.NumberPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslNoOffsetNumberOptionsTest : FunSpec({

    test("should extract field name from NumberPath correctly") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "testEntity.id"

        val options = QuerydslNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "id"
    }

    test("should extract field name correctly with nested path") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "testEntity.order.totalAmount"

        val options = QuerydslNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "totalAmount"
    }

    test("should work with different number types - Long") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "testEntity.id"

        val options = QuerydslNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "id"
    }

    test("should work with different number types - Integer") {
        val numberPath = mockk<NumberPath<Int>>()
        every { numberPath.toString() } returns "testEntity.count"

        val options = QuerydslNoOffsetNumberOptions<Any, Int>(
            field = numberPath,
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "count"
    }

    test("should work with ASC expression") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "testEntity.sequence"

        val options = QuerydslNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            expression = Expression.ASC
        )

        val expressionField = options::class.java.superclass.superclass.getDeclaredField("expression")
        expressionField.isAccessible = true
        val expression = expressionField.get(options) as Expression

        expression shouldBe Expression.ASC
    }

    test("should work with DESC expression") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "testEntity.sequence"

        val options = QuerydslNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            expression = Expression.DESC
        )

        val expressionField = options::class.java.superclass.superclass.getDeclaredField("expression")
        expressionField.isAccessible = true
        val expression = expressionField.get(options) as Expression

        expression shouldBe Expression.DESC
    }

    test("should handle single word path") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.toString() } returns "id"

        val options = QuerydslNoOffsetNumberOptions<Any, Long>(
            field = numberPath,
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "id"
    }
})
