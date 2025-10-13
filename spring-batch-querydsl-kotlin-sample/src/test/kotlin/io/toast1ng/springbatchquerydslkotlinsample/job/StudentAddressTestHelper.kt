package io.toast1ng.springbatchquerydslkotlinsample.job

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentAddressJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import io.toast1ng.springbatchquerydslkotlinsample.db.repository.StudentAddressJpaRepository

/**
 * Helper class for managing StudentAddress test data.
 */
class StudentAddressTestHelper(
    private val studentAddressJpaRepository: StudentAddressJpaRepository
) {

    /**
     * Creates and saves addresses for given students.
     */
    fun createAddresses(
        students: List<StudentJpaEntity>,
        addressSeqNoOffset: Long = 100L
    ): List<StudentAddressJpaEntity> {
        val addresses = students.map { student ->
            StudentAddressJpaEntity(
                seqNo = student.seqNo + addressSeqNoOffset,
                student = student,
                address = "Address ${student.seqNo}"
            )
        }
        return studentAddressJpaRepository.saveAll(addresses)
    }

    /**
     * Cleans up all student address test data.
     */
    fun cleanUp() {
        studentAddressJpaRepository.deleteAll()
    }
}
