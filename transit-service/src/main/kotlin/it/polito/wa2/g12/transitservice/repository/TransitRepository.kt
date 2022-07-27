package it.polito.wa2.g12.transitservice.repository

import it.polito.wa2.g12.transitservice.entity.Transit
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface TransitRepository : CoroutineCrudRepository<Transit, Long>{
    @Query(
        """
        SELECT *
        FROM transits
    """
    )
    fun findAllTransit(): Flow<Transit>
}