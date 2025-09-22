package io.toast1ng.springbatchquerydslkotlinsample.job.processor

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentAddressProjection
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import org.springframework.batch.item.ItemProcessor

class SampleProjectionItemProcessor : ItemProcessor<StudentAddressProjection, StudentJpaEntity> {
    override fun process(item: StudentAddressProjection): StudentJpaEntity? {
        println("processing $item")
        return StudentJpaEntity(
            seqNo = item.studentSeqNo,
            name = item.studentName,
            age = item.studentAge,
            isProcessed = true
        )
    }
}