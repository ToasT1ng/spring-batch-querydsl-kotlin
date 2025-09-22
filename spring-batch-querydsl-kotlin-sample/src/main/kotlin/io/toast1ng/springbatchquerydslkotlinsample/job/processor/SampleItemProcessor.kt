package io.toast1ng.springbatchquerydslkotlinsample.job.processor

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import org.springframework.batch.item.ItemProcessor

class SampleItemProcessor : ItemProcessor<StudentJpaEntity, StudentJpaEntity> {
    override fun process(item: StudentJpaEntity): StudentJpaEntity {
        item.updateIsProcessed()
        println("processing $item")
        return item
    }
}