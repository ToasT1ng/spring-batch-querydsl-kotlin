package io.toast1ng.springbatchquerydslkotlinsample.job

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentAddressProjection
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.repository.StudentJpaRepository
import io.toast1ng.springbatchquerydslkotlinsample.job.processor.SampleProjectionItemProcessor
import io.toast1ng.springbatchquerydslkotlinsample.job.reader.SampleProjectionItemReader
import io.toast1ng.springbatchquerydslkotlinsample.job.writer.SampleItemWriter
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.querydsl.reader.QuerydslNoOffsetPagingItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SampleProjectionStepDetailConfig {
    @StepScope
    @Bean
    fun sampleProjectionItemReader(
        entityManagerFactory: EntityManagerFactory,
    ): QuerydslNoOffsetPagingItemReader<StudentAddressProjection> {
        return SampleProjectionItemReader(entityManagerFactory, CHUNK_SIZE)
    }

    @StepScope
    @Bean
    fun sampleProjectionItemProcessor(): ItemProcessor<StudentAddressProjection, StudentJpaEntity> {
        return SampleProjectionItemProcessor()
    }

    @StepScope
    @Bean
    fun sampleProjectionItemWriter(
        studentJpaRepository: StudentJpaRepository,
    ): ItemWriter<StudentJpaEntity> {
        return SampleItemWriter(studentJpaRepository)
    }
}