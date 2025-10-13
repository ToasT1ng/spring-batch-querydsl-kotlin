package io.toast1ng.springbatchquerydslkotlinsample.config

import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

/**
 * Common annotation for Spring Batch integration tests.
 * Ensures Spring context is reused across tests.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@SpringBatchTest
@TestPropertySource(
    properties = [
        "spring.batch.job.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
    ]
)
annotation class BatchIntegrationTest
