package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.StringPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslNoOffsetStringOptionsTest : FunSpec({

    test("should extract field name from StringPath correctly") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "testEntity.name"

        val options = QuerydslNoOffsetStringOptions<Any>(
            field = stringPath,
            expression = Expression.ASC
        )

        // Field name should be extracted as "name" from "testEntity.name"
        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "name"
    }

    test("should extract field name correctly with nested path") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "testEntity.address.city"

        val options = QuerydslNoOffsetStringOptions<Any>(
            field = stringPath,
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "city"
    }

    test("should work with ASC expression") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "testEntity.status"

        val options = QuerydslNoOffsetStringOptions<Any>(
            field = stringPath,
            expression = Expression.ASC
        )

        val expressionField = options::class.java.superclass.superclass.getDeclaredField("expression")
        expressionField.isAccessible = true
        val expression = expressionField.get(options) as Expression

        expression shouldBe Expression.ASC
    }

    test("should work with DESC expression") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "testEntity.status"

        val options = QuerydslNoOffsetStringOptions<Any>(
            field = stringPath,
            expression = Expression.DESC
        )

        val expressionField = options::class.java.superclass.superclass.getDeclaredField("expression")
        expressionField.isAccessible = true
        val expression = expressionField.get(options) as Expression

        expression shouldBe Expression.DESC
    }

    test("should handle single word path") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "name"

        val options = QuerydslNoOffsetStringOptions<Any>(
            field = stringPath,
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "name"
    }
})
