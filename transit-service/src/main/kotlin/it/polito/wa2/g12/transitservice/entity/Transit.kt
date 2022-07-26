package it.polito.wa2.g12.transitservice.entity

import it.polito.wa2.g12.transitservice.dto.TransitDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("transits")
class Transit(
    @Column
    var transit_date: LocalDateTime,
    @Column
    var ticket_id: Long,
    @Column
    var ticket_type : String,
    @Column
    var ticket_user : String,
) {
    @Id
    var id :Long? = null
}

fun Transit.toDTO() = TransitDTO(id!!,transit_date,ticket_id,ticket_type,ticket_user)