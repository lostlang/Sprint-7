package com.example.demo

import com.example.demo.persistance.Entity
import com.example.demo.persistance.EntityRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class EntityRepositoryTest {
    @Autowired
    private lateinit var entityRepository: EntityRepository

    @BeforeEach
    fun setUp(){
        entityRepository.save(Entity(1, "Alec"))
        entityRepository.save(Entity(2, "Alexander"))
    }

    @Test
    fun findAll(){
        val entitiesSize = entityRepository.count()

        assertTrue { entitiesSize == 2L }
    }

    @AfterEach
    fun drop() {
        entityRepository.deleteAll()
    }
}