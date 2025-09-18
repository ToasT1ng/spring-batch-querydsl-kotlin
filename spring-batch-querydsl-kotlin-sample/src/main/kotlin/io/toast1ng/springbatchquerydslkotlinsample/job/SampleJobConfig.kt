package io.toast1ng.springbatchquerydslkotlinsample.job

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
class SampleJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {
    @Bean
    fun sampleJob(
        sampleStep: Step,
        printStep: Step,
    ): Job {
        return JobBuilder("sampleJob", jobRepository)
            .start(sampleStep)
            .next(printStep)
            .build()
    }

    @Bean
    fun sampleStep(
        sampleItemReader: QuerydslNoOffsetPagingItemReader<StudentJpaEntity>,
        sampleItemProcessor: ItemProcessor<StudentJpaEntity, StudentJpaEntity>,
        sampleItemWriter: ItemWriter<StudentJpaEntity>,
    ): Step {
        return StepBuilder("sampleStep", jobRepository)
            .chunk<StudentJpaEntity, StudentJpaEntity>(
                CHUNK_SIZE,
                transactionManager
            )
            .reader(sampleItemReader)
            .processor(sampleItemProcessor)
            .writer(sampleItemWriter)
            .build()
    }

    @Bean
    fun printStep(
        studentJpaRepository: StudentJpaRepository,
    ): Step {
        return StepBuilder("printStep", jobRepository)
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