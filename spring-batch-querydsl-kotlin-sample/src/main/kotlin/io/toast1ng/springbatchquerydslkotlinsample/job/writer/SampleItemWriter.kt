package io.toast1ng.springbatchquerydslkotlinsample.job.writer

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.repository.StudentJpaRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter

class SampleItemWriter(
    private val studentJpaRepository: StudentJpaRepository,
) : ItemWriter<StudentJpaEntity> {
    override fun write(chunk: Chunk<out StudentJpaEntity?>) {
        studentJpaRepository.saveAll(chunk.items)
    }
}