package it.polito.wa2.g12.reportservice.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("transits")
class Transit(
    @Column var ticket_type: String,
    @Column var username: String,
    @Column var transit_date: LocalDateTime,
) {
    @Id
    var id: Long? = null
}