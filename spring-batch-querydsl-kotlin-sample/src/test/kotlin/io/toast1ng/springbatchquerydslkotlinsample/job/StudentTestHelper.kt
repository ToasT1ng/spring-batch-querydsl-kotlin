package io.toast1ng.springbatchquerydslkotlinsample.job

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.repository.StudentJpaRepository

/**
 * Helper class for managing Student test data.
 */
class StudentTestHelper(
    private val studentJpaRepository: StudentJpaRepository
) {

    /**
     * Creates and saves students.
     */
    fun createStudents(count: Int, startId: Long = 1L, baseAge: Int = 20): List<StudentJpaEntity> {
        val students = (startId until startId + count).map { id ->
            StudentJpaEntity(
                seqNo = id,
                name = "Student$id",
                age = baseAge + ((id - startId) % 10).toInt(),
                isProcessed = false
            )
        }
        return studentJpaRepository.saveAll(students)
    }

    /**
     * Cleans up all student test data.
     */
    fun cleanUp() {
        studentJpaRepository.deleteAll()
    }
}
