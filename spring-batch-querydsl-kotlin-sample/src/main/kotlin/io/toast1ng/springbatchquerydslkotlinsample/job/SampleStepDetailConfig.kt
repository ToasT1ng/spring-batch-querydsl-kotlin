package io.toast1ng.springbatchquerydslkotlinsample.job

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.repository.StudentJpaRepository
import io.toast1ng.springbatchquerydslkotlinsample.job.processor.SampleItemProcessor
import io.toast1ng.springbatchquerydslkotlinsample.job.reader.SampleItemReader
import io.toast1ng.springbatchquerydslkotlinsample.job.writer.SampleItemWriter
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.querydsl.reader.QuerydslNoOffsetPagingItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SampleStepDetailConfig {
    @StepScope
    @Bean
    fun sampleItemReader(
        entityManagerFactory: EntityManagerFactory,
    ): QuerydslNoOffsetPagingItemReader<StudentJpaEntity> {
        return SampleItemReader(entityManagerFactory, CHUNK_SIZE)
    }

    @StepScope
    @Bean
    fun sampleItemProcessor(): ItemProcessor<StudentJpaEntity, StudentJpaEntity> {
        return SampleItemProcessor()
    }

    @StepScope
    @Bean
    fun sampleItemWriter(
        studentJpaRepository: StudentJpaRepository,
    ): ItemWriter<StudentJpaEntity> {
        return SampleItemWriter(studentJpaRepository)
    }
}