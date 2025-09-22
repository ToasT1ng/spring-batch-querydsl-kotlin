package io.toast1ng.springbatchquerydslkotlinsample.db.entity

import jakarta.persistence.*


@Table(name = "student_address")
@Entity
class StudentAddressJpaEntity(
    @Id
    @Column(name = "seq_no")
    val seqNo: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_seq_no", nullable = false)
    val student: StudentJpaEntity,

    @Column(name = "address", nullable = false, length = 100)
    val address: String
) {
    override fun toString(): String {
        return "StudentAddressJpaEntity(seqNo=$seqNo, studentSeqNo=${student.seqNo}, address='$address')"
    }
}