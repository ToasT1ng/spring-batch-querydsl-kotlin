package io.toast1ng.springbatchquerydslkotlinsample.db.repository

import io.toast1ng.springbatchquerydslkotlinsample.db.entity.StudentAddressJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StudentAddressJpaRepository : JpaRepository<StudentAddressJpaEntity, Long>
