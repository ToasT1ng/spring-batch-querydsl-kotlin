package io.toast1ng.springbatchquerydslkotlinsample.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "student")
@Entity
class StudentJpaEntity(
    @Id
    @Column(name = "seq_no")
    val seqNo: Long,
    val name: String,
    val age: Int,
    @Column(name = "is_processed")
    var isProcessed: Boolean = false
) {

    fun updateIsProcessed() {
        this.isProcessed = true
    }

    override fun toString(): String {
        return "StudentJpaEntity(seqNo=$seqNo, name='$name', age=$age, isProcessed=$isProcessed)"
    }
}