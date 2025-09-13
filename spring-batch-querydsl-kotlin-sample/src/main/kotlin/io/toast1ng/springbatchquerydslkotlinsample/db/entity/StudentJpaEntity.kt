package io.toast1ng.springbatchquerydslkotlinsample.db.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "student")
@Entity
class StudentJpaEntity(
    @Id
    val seqNo: Long,
    val name: String,
    val age: Int,
)