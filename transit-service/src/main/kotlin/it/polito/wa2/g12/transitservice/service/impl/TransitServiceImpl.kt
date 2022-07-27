package it.polito.wa2.g12.transitservice.service.impl

import it.polito.wa2.g12.transitservice.dto.TimePeriodDTO
import it.polito.wa2.g12.transitservice.dto.TicketDTO
import it.polito.wa2.g12.transitservice.dto.TransitDTO
import it.polito.wa2.g12.transitservice.dto.TransitsStatsDTO
import it.polito.wa2.g12.transitservice.entity.Transit
import it.polito.wa2.g12.transitservice.entity.toDTO
import it.polito.wa2.g12.transitservice.message.SuccessfulTransitMessage
import it.polito.wa2.g12.transitservice.repository.TransitRepository
import it.polito.wa2.g12.transitservice.service.TransitService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class  TransitServiceImpl : TransitService {

    @Value("\${kafka.topics.successfulTransit}")
    lateinit var topicSuccessfulTransit: String

    @Autowired
    @Qualifier("successfulTransitKafkaTemplate")
    lateinit var kafkaSuccessfulTransitTemplate: KafkaTemplate<String, Any>

    @Autowired
    lateinit var transitRepository: TransitRepository

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun getAllTransits(): Flow<TransitDTO> {
        return transitRepository.findAllTransit().map { it.toDTO() }
    }

    override suspend fun getRepTransits(datarange: TimePeriodDTO): TransitsStatsDTO {
        val transitsCounter = transitRepository.findAllTransit().map { it.transit_date }.filter {
            it.isAfter(LocalDateTime.parse(datarange.start_date, formatter)) &&
            it.isBefore(LocalDateTime.parse(datarange.end_date, formatter))
        }.count()
        return TransitsStatsDTO(
            transitsCounter,
                 100 * transitRepository.findAllTransit().filter {
                    it.transit_date.isAfter(LocalDateTime.parse(datarange.start_date, formatter)) &&
                    it.transit_date.isBefore(LocalDateTime.parse(datarange.end_date, formatter)) &&
                    it.ticket_type == "Ordinary"
                }.count().toFloat() / transitsCounter.toFloat(),
                 100 * transitRepository.findAllTransit().filter {
                    it.transit_date.isAfter(LocalDateTime.parse(datarange.start_date, formatter)) &&
                    it.transit_date.isBefore(LocalDateTime.parse(datarange.end_date, formatter)) &&
                    it.ticket_type != "Ordinary"
                }.count().toFloat() / transitsCounter.toFloat()
        )
    }

    override suspend fun getUserRepTransits(datarange: TimePeriodDTO, username: String): TransitsStatsDTO {
        val transitsCounter = transitRepository.findAllTransit().filter {
              it.ticket_user == username &&
              it.transit_date.isAfter(LocalDateTime.parse(datarange.start_date, formatter)) &&
              it.transit_date.isBefore(LocalDateTime.parse(datarange.end_date, formatter))
        }.count()
        return TransitsStatsDTO(
            transitsCounter,
            100 * transitRepository.findAllTransit().filter {
                it.transit_date.isAfter(LocalDateTime.parse(datarange.start_date, formatter)) &&
                it.transit_date.isBefore(LocalDateTime.parse(datarange.end_date, formatter)) &&
                it.ticket_user == username &&
                it.ticket_type == "Ordinary"
            }.count().toFloat() / transitsCounter.toFloat(),
            100 * transitRepository.findAllTransit().filter {
                it.transit_date.isAfter(LocalDateTime.parse(datarange.start_date, formatter)) &&
                it.transit_date.isBefore(LocalDateTime.parse(datarange.end_date, formatter)) &&
                it.ticket_user == username &&
                it.ticket_type != "Ordinary"
            }.count().toFloat() / transitsCounter.toFloat()
        )
    }

    override suspend fun insertNewTransit(ticket : TicketDTO): TransitDTO {
        val now = LocalDateTime.now()
        val transitEntity = Transit(now,ticket.ticket_id,ticket.ticket_type,ticket.user)
        val transitDTO = transitRepository.save(transitEntity).toDTO()

        val successfulTransitMessage: Message<SuccessfulTransitMessage> = MessageBuilder
            .withPayload(
                SuccessfulTransitMessage(
                    transitDTO.id,
                    transitDTO.ticket_type,
                    transitDTO.ticket_user,
                    transitDTO.transit_date.toString()
                )
            )
            .setHeader(KafkaHeaders.TOPIC, topicSuccessfulTransit)
            .setHeader("X-Custom-Header", "Custom header here")
            .build()
        kafkaSuccessfulTransitTemplate.send(successfulTransitMessage)

        return transitDTO
    }
}