package io.toast1ng.springbatchquerydslkotlinsample.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
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
    ): Job {
        return JobBuilder("sampleJob", jobRepository)
            .start(sampleStep)
            .build()
    }

    @Bean
    fun sampleStep(): Step {
        return StepBuilder("sampleStep", jobRepository)
            .tasklet({ _, _ ->
                println("Sample Step executed")
                null
            }, transactionManager)
            .build()
    }
}