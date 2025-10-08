package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.StringPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.batch.item.querydsl.reader.expression.Expression

class QuerydslProjectionNoOffsetStringOptionsTest : FunSpec({

    test("should use provided fieldName for projections") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "concat(testEntity.firstName, ' ', testEntity.lastName)"

        val options = QuerydslProjectionNoOffsetStringOptions<Any>(
            field = stringPath,
            fieldName = "fullName",
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        // Should use provided fieldName, not extract from path
        fieldName shouldBe "fullName"
    }

    test("should work with projection queries") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "upper(testEntity.status)"

        val options = QuerydslProjectionNoOffsetStringOptions<Any>(
            field = stringPath,
            fieldName = "upperStatus",
            expression = Expression.DESC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "upperStatus"
    }

    test("should properly initialize with StringPath and fieldName") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "substring(testEntity.code, 0, 3)"

        val options = QuerydslProjectionNoOffsetStringOptions<Any>(
            field = stringPath,
            fieldName = "codePrefix",
            expression = Expression.ASC
        )

        val expressionField = options::class.java.superclass.superclass.getDeclaredField("expression")
        expressionField.isAccessible = true
        val expression = expressionField.get(options) as Expression

        expression shouldBe Expression.ASC
    }

    test("should work with ASC expression") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "lower(name)"

        val options = QuerydslProjectionNoOffsetStringOptions<Any>(
            field = stringPath,
            fieldName = "lowerName",
            expression = Expression.ASC
        )

        val expressionField = options::class.java.superclass.superclass.getDeclaredField("expression")
        expressionField.isAccessible = true
        val expression = expressionField.get(options) as Expression

        expression shouldBe Expression.ASC
    }

    test("should work with DESC expression") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "trim(description)"

        val options = QuerydslProjectionNoOffsetStringOptions<Any>(
            field = stringPath,
            fieldName = "trimmedDescription",
            expression = Expression.DESC
        )

        val expressionField = options::class.java.superclass.superclass.getDeclaredField("expression")
        expressionField.isAccessible = true
        val expression = expressionField.get(options) as Expression

        expression shouldBe Expression.DESC
    }

    test("should handle complex string functions") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "coalesce(testEntity.nickname, testEntity.name)"

        val options = QuerydslProjectionNoOffsetStringOptions<Any>(
            field = stringPath,
            fieldName = "displayName",
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "displayName"
    }

    test("should handle simple field name for projections") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "max(category)"

        val options = QuerydslProjectionNoOffsetStringOptions<Any>(
            field = stringPath,
            fieldName = "maxCategory",
            expression = Expression.DESC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "maxCategory"
    }

    test("should work with group by projections") {
        val stringPath = mockk<StringPath>()
        every { stringPath.toString() } returns "testEntity.region"

        val options = QuerydslProjectionNoOffsetStringOptions<Any>(
            field = stringPath,
            fieldName = "groupRegion",
            expression = Expression.ASC
        )

        val fieldNameField = options::class.java.superclass.superclass.getDeclaredField("fieldName")
        fieldNameField.isAccessible = true
        val fieldName = fieldNameField.get(options) as String

        fieldName shouldBe "groupRegion"
    }
})
