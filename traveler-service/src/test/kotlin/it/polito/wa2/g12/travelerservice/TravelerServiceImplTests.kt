package it.polito.wa2.g12.travelerservice

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import it.polito.wa2.g12.travelerservice.dto.UserInfoDTO
import it.polito.wa2.g12.travelerservice.entities.TicketPurchased
import it.polito.wa2.g12.travelerservice.entities.UserDetails
import it.polito.wa2.g12.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.g12.travelerservice.repositories.UserDetailsRepository
import it.polito.wa2.g12.travelerservice.service.impl.TravelerServiceImpl
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class TravelerServiceImplTests {

    @InjectMocks
    lateinit var travelerServiceImpl: TravelerServiceImpl

    @Mock
    lateinit var ticketsRepo: TicketPurchasedRepository

    @Mock
    lateinit var userDetRepo: UserDetailsRepository

    @BeforeEach
    fun setUp() {
        val testKey = "TestKeyTestKeyTestKeyTestKeyTestKeyTestKeyTestKey"
        MockitoAnnotations.initMocks(this)
        ReflectionTestUtils.setField(
            travelerServiceImpl,
            "secretKey",
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(testKey))
        )
    }

    @Test
    fun getUserDetTest() {
        Mockito.`when`(userDetRepo.findByName("test")).thenReturn(
            Optional.of(UserDetails("test", "test", "test", Date(0), "0123456789"))
        )
        val notNullUserInfoDto = travelerServiceImpl.getUserDet("test")
        Mockito.verify(userDetRepo, times(2)).findByName("test")
        Mockito.clearInvocations(userDetRepo)
        assertNotNull(notNullUserInfoDto)
        assert(notNullUserInfoDto!!.name == "test")

        Mockito.`when`(userDetRepo.findByName("test")).thenReturn(Optional.empty())
        val nullUserInfoDto = travelerServiceImpl.getUserDet("test")
        Mockito.verify(userDetRepo, times(1)).findByName("test")
        Mockito.clearInvocations(userDetRepo)
        assertNull(nullUserInfoDto)
    }

    @Test
    fun getUserDetByIdTest() {
        Mockito.`when`(userDetRepo.findById(1)).thenReturn(
            Optional.of(UserDetails("test", "test", "test", Date(0), "0123456789"))
        )
        val notNullUserInfoDto = travelerServiceImpl.getUserDetById(1)
        Mockito.verify(userDetRepo).findById(1)
        Mockito.clearInvocations(userDetRepo)
        assertNotNull(notNullUserInfoDto)
        assert(notNullUserInfoDto!!.name == "test")

        Mockito.`when`(userDetRepo.findById(1)).thenReturn(Optional.empty())
        val nullUserInfoDto = travelerServiceImpl.getUserDetById(1)
        Mockito.verify(userDetRepo).findById(1)
        Mockito.clearInvocations(userDetRepo)
        assertNull(nullUserInfoDto)
    }

    @Test
    fun updateUserDetTest() {
        val userInfoDto = UserInfoDTO("testA", "test", Date(0), "0123456789")
        val userDetails = UserDetails("test", "testA", "test", Date(0), "0123456789")

        Mockito.`when`(userDetRepo.findByName("testB")).thenReturn(Optional.empty())
        val res1 = travelerServiceImpl.updateUserDet("testB", userInfoDto)
        assert(res1 == 0)

        Mockito.`when`(userDetRepo.findByName("testA")).thenReturn(Optional.of(userDetails))
        Mockito.`when`(userDetRepo.findByName("testB")).thenReturn(Optional.of(userDetails))
        val res2 = travelerServiceImpl.updateUserDet("testB", userInfoDto)
        assert(res2 == 1)

        /*
        Mockito.`when`(userDetRepo.findByName("testA")).thenReturn(Optional.empty())
        val res3 = travelerServiceImpl.updateUserDet("testB", userInfoDto)
        assert(res3 == 2)
        */
    }

    @Test
    fun getUserTicketsTest() {
        val userDetails = UserDetails("test", "test", "test", Date(0), "0123456789")
        Mockito.`when`(userDetRepo.findByName("test")).thenReturn(
            Optional.of(userDetails)
        )
        userDetails.setId(1L)
        Mockito.`when`(ticketsRepo.findAllByUserDet(1L)).thenReturn(
            listOf("1,2022-05-20 03:55:21.000000,2022-05-20 03:55:21.000000,A,1,2022-05-20 03:55:21.000000,B")
        )
        val ticketList = travelerServiceImpl.getUserTickets("test")
        Mockito.clearInvocations(userDetRepo)
        Mockito.clearInvocations(ticketsRepo)
        assertNotNull(ticketList)
    }


    @Test
    fun getTicketsByUserIdTest() {
        Mockito.`when`(userDetRepo.findById(1L)).thenReturn(
            Optional.empty()
        )
        val ticketList = travelerServiceImpl.getTicketsByUserId(1L)
        Mockito.clearInvocations(userDetRepo)
        Mockito.clearInvocations(ticketsRepo)
        assertNull(ticketList)
    }

    @Test
    fun createUserTicketsTest() {
        val userDetails = UserDetails("test", "test", "test", Date(0), "0123456789")
        Mockito.`when`(userDetRepo.findByName("test")).thenReturn(
            Optional.of(userDetails)
        )
        val ticketPurchased = TicketPurchased("A", userDetails)
        ticketPurchased.setId(1L)
        Mockito.`when`(ticketsRepo.save(any())).thenReturn(
            ticketPurchased
        )
        val newTickets = travelerServiceImpl.createUserTickets("test", 2, "A")
        Mockito.clearInvocations(userDetRepo)
        Mockito.clearInvocations(ticketsRepo)
        assertNotNull(newTickets)
    }

    @Test
    fun getTravelersTest() {
        Mockito.`when`(userDetRepo.findAllTravelers()).thenReturn(
            listOf("test1", "test2", "test3")
        )
        val travelers = travelerServiceImpl.getTravelers()
        for (i in 0..2) {
            assert(travelers[i] == "test" + (i + 1))
        }
        Mockito.clearInvocations(userDetRepo)
    }
}