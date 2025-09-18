package io.toast1ng.springbatchquerydslkotlinsample.job.reader

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.QStudentJpaEntity.studentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.item.querydsl.reader.QuerydslNoOffsetPagingItemReader
import org.springframework.batch.item.querydsl.reader.expression.Expression
import org.springframework.batch.item.querydsl.reader.options.QuerydslNoOffsetNumberOptions

class SampleItemReader(
    override val entityManagerFactory: EntityManagerFactory,
    private val pageSize: Int,
) : QuerydslNoOffsetPagingItemReader<StudentJpaEntity>(
    entityManagerFactory = entityManagerFactory,
    pageSize = pageSize,
    queryFunction = { queryFactory ->
        queryFactory.selectFrom(studentJpaEntity)
    },
    options = QuerydslNoOffsetNumberOptions(
        field = studentJpaEntity.seqNo,
        expression = Expression.ASC
    )
)