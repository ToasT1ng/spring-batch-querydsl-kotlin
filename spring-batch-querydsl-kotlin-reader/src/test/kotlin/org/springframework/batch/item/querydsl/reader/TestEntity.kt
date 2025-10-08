package org.springframework.batch.item.querydsl.reader

import jakarta.persistence.*

@Entity
@Table(name = "test_entity")
data class TestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String = "",

    val value: Int = 0
)
