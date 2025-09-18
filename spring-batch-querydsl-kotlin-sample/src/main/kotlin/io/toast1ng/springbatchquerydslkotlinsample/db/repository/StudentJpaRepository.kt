package io.toast1ng.springbatchquerydslkotlinsample.db.repository

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StudentJpaRepository : JpaRepository<StudentJpaEntity, Long>