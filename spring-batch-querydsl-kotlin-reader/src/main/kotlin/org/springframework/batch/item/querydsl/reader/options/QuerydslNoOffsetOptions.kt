package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.Path
import com.querydsl.jpa.impl.JPAQuery
import org.springframework.batch.item.querydsl.reader.expression.Expression
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import java.lang.reflect.Field

abstract class QuerydslNoOffsetOptions<T>(
    field: Path<*>,
    protected val expression: Expression
) {
    protected val logger: Log = LogFactory.getLog(this::class.java)
    protected val fieldName: String

    init {
        val qField = field.toString().split(".")
        this.fieldName = qField.last()

        if (logger.isDebugEnabled) {
            logger.debug("fieldName= $fieldName")
        }
    }

    abstract fun initKeys(query: JPAQuery<T>, page: Int)

    protected abstract fun initFirstId(query: JPAQuery<T>)
    protected abstract fun initLastId(query: JPAQuery<T>)

    abstract fun createQuery(query: JPAQuery<T>, page: Int): JPAQuery<T>

    abstract fun resetCurrentId(item: T)

    protected fun getFieldValue(item: T): Any? {
        return try {
            val field: Field = item!!::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            field.get(item)
        } catch (e: NoSuchFieldException) {
            logger.error("Not Found Field= $fieldName", e)
            throw IllegalArgumentException("Not Found Field", e)
        } catch (e: IllegalAccessException) {
            logger.error("Not Access Field= $fieldName", e)
            throw IllegalArgumentException("Not Access Field", e)
        }
    }

    fun isGroupByQuery(query: JPAQuery<T>): Boolean =
        isGroupByQuery(query.toString())

    fun isGroupByQuery(sql: String): Boolean =
        sql.lowercase().contains("group by")
}