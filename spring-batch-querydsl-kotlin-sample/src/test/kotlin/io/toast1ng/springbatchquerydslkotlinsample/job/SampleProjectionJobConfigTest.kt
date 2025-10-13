package io.toast1ng.springbatchquerydslkotlinsample.job

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentAddressJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.repository.StudentAddressJpaRepository
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
class SampleProjectionJobConfigTest(
    @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,
    @Autowired private val sampleProjectionJob: Job,
    @Autowired private val studentJpaRepository: StudentJpaRepository,
    @Autowired private val studentAddressJpaRepository: StudentAddressJpaRepository
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    private lateinit var studentTestHelper: StudentTestHelper
    private lateinit var studentAddressTestHelper: StudentAddressTestHelper

    init {
        beforeEach {
            studentTestHelper = StudentTestHelper(studentJpaRepository)
            studentAddressTestHelper = StudentAddressTestHelper(studentAddressJpaRepository)
            studentAddressTestHelper.cleanUp()
            studentTestHelper.cleanUp()
            jobLauncherTestUtils.job = sampleProjectionJob
        }

        afterEach {
            studentAddressTestHelper.cleanUp()
            studentTestHelper.cleanUp()
        }

        test("sampleProjectionJob should process students with addresses") {
            // Given
            val students = studentTestHelper.createStudents(count = 15)
            studentAddressTestHelper.createAddresses(students)

            // When
            val jobParameters = JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()

            val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

            // Then
            jobExecution.status shouldBe BatchStatus.COMPLETED

            val processedStudents = studentJpaRepository.findAll()
            processedStudents.size shouldBe 15
            processedStudents.all { it.isProcessed } shouldBe true
        }

        test("sampleProjectionJob should handle empty dataset") {
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

        test("sampleProjectionJob should process in chunks of 10") {
            // Given
            val students = studentTestHelper.createStudents(count = 25)
            studentAddressTestHelper.createAddresses(students)

            // When
            val jobParameters = JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()

            val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

            // Then
            jobExecution.status shouldBe BatchStatus.COMPLETED

            val stepExecution = jobExecution.stepExecutions.first { it.stepName == "sampleProjectionStep" }
            stepExecution.readCount shouldBe 25
            stepExecution.writeCount shouldBe 25
            stepExecution.commitCount shouldBe 3 // 3 chunks: 10 + 10 + 5
        }

        test("sampleProjectionJob should execute both steps in order") {
            // Given
            val students = studentTestHelper.createStudents(count = 5)
            studentAddressTestHelper.createAddresses(students)

            // When
            val jobParameters = JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()

            val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

            // Then
            jobExecution.status shouldBe BatchStatus.COMPLETED

            val stepNames = jobExecution.stepExecutions.map { it.stepName }
            stepNames shouldBe listOf("sampleProjectionStep", "printStep2")

            jobExecution.stepExecutions.all { it.status == BatchStatus.COMPLETED } shouldBe true
        }
    }
}
