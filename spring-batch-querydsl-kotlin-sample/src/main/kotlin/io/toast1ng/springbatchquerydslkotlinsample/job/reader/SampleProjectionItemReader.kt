package io.toast1ng.springbatchquerydslkotlinsample.job.reader

import com.querydsl.core.types.Projections
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.QStudentAddressJpaEntity.studentAddressJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.QStudentJpaEntity.studentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentAddressProjection
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.item.querydsl.reader.QuerydslNoOffsetPagingItemReader
import org.springframework.batch.item.querydsl.reader.expression.Expression
import org.springframework.batch.item.querydsl.reader.options.QuerydslProjectionNoOffsetNumberOptions

class SampleProjectionItemReader(
    override val entityManagerFactory: EntityManagerFactory,
    private val pageSize: Int,
) : QuerydslNoOffsetPagingItemReader<StudentAddressProjection>(
    entityManagerFactory = entityManagerFactory,
    pageSize = pageSize,
    queryFunction = { queryFactory ->
        queryFactory.select(
            Projections.constructor(
                StudentAddressProjection::class.java,
                studentJpaEntity.seqNo,
                studentAddressJpaEntity.seqNo,
                studentJpaEntity.name,
                studentJpaEntity.age,
                studentAddressJpaEntity.address
            )
        )
            .from(studentJpaEntity)
            .join(studentAddressJpaEntity)
            .on(studentJpaEntity.seqNo.eq(studentAddressJpaEntity.student.seqNo))
    },
    options = QuerydslProjectionNoOffsetNumberOptions(
        field = studentAddressJpaEntity.seqNo,
        expression = Expression.ASC,
        fieldName = "studentAddressSeqNo"
    )
)