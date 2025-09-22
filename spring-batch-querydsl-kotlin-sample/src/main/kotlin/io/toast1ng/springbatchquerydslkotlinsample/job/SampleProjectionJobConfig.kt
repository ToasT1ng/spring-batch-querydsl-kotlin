package io.toast1ng.springbatchquerydslkotlinsample.job

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentAddressProjection
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.repository.StudentJpaRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.querydsl.reader.QuerydslNoOffsetPagingItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class SampleProjectionJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {
    @Bean
    fun sampleProjectionJob(
        sampleProjectionStep: Step,
        printStep2: Step,
    ): Job {
        return JobBuilder("sampleProjectionJob", jobRepository)
            .start(sampleProjectionStep)
            .next(printStep2)
            .build()
    }

    @Bean
    fun sampleProjectionStep(
        sampleProjectionItemReader: QuerydslNoOffsetPagingItemReader<StudentAddressProjection>,
        sampleProjectionItemProcessor: ItemProcessor<StudentAddressProjection, StudentJpaEntity>,
        sampleProjectionItemWriter: ItemWriter<StudentJpaEntity>,
    ): Step {
        return StepBuilder("sampleProjectionStep", jobRepository)
            .chunk<StudentAddressProjection, StudentJpaEntity>(
                CHUNK_SIZE,
                transactionManager
            )
            .reader(sampleProjectionItemReader)
            .processor(sampleProjectionItemProcessor)
            .writer(sampleProjectionItemWriter)
            .build()
    }

    @Bean
    fun printStep2(
        studentJpaRepository: StudentJpaRepository,
    ): Step {
        return StepBuilder("printStep2", jobRepository)
            .tasklet({ _, _ ->
                val students = studentJpaRepository.findAll()
                students.forEach {
                    println(it.toString())
                }
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}