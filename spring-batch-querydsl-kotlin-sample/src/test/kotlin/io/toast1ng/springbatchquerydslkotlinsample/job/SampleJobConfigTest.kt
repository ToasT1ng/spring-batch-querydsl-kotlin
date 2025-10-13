package io.toast1ng.springbatchquerydslkotlinsample.job

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.repository.StudentJpaRepository
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@SpringBatchTest
@TestPropertySource(
    properties = [
        "spring.batch.job.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
    ]
)
class SampleJobConfigTest(
    @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,
    @Autowired private val sampleJob: Job,
    @Autowired private val studentJpaRepository: StudentJpaRepository
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        beforeEach {
            studentJpaRepository.deleteAll()
            jobLauncherTestUtils.job = sampleJob
        }

        afterEach {
            studentJpaRepository.deleteAll()
        }

        test("sampleJob should process all students and update isProcessed flag") {
            // Given
            val students = (1L..25L).map { id ->
                StudentJpaEntity(
                    seqNo = id,
                    name = "Student$id",
                    age = 20 + (id % 10).toInt(),
                    isProcessed = false
                )
            }
            studentJpaRepository.saveAll(students)

            // When
            val jobParameters = JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()

            val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

            // Then
            jobExecution.status shouldBe BatchStatus.COMPLETED

            val processedStudents = studentJpaRepository.findAll()
            processedStudents.size shouldBe 25
            processedStudents.all { it.isProcessed } shouldBe true
        }

        test("sampleJob should handle empty dataset") {
            // When
            val jobParameters = JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()

            val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

            // Then
            jobExecution.status shouldBe BatchStatus.COMPLETED

            val students = studentJpaRepository.findAll()
            students.size shouldBe 0
        }

        test("sampleJob should process students in chunks of 10") {
            // Given
            val students = (1L..15L).map { id ->
                StudentJpaEntity(
                    seqNo = id,
                    name = "Student$id",
                    age = 20,
                    isProcessed = false
                )
            }
            studentJpaRepository.saveAll(students)

            // When
            val jobParameters = JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()

            val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

            // Then
            jobExecution.status shouldBe BatchStatus.COMPLETED

            val stepExecution = jobExecution.stepExecutions.first { it.stepName == "sampleStep" }
            stepExecution.readCount shouldBe 15
            stepExecution.writeCount shouldBe 15
            stepExecution.commitCount shouldBe 2 // 2 chunks: 10 + 5
        }

        test("sampleJob should execute both steps in order") {
            // Given
            val students = (1L..5L).map { id ->
                StudentJpaEntity(
                    seqNo = id,
                    name = "Student$id",
                    age = 20,
                    isProcessed = false
                )
            }
            studentJpaRepository.saveAll(students)

            // When
            val jobParameters = JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()

            val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

            // Then
            jobExecution.status shouldBe BatchStatus.COMPLETED

            val stepNames = jobExecution.stepExecutions.map { it.stepName }
            stepNames shouldBe listOf("sampleStep", "printStep1")

            jobExecution.stepExecutions.all { it.status == BatchStatus.COMPLETED } shouldBe true
        }
    }
}
