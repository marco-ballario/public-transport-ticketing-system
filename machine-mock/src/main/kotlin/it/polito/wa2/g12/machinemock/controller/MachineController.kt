package it.polito.wa2.g12.machinemock.controller

import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import io.jsonwebtoken.Jwts
import it.polito.wa2.g12.machinemock.dto.LoginDTO
import it.polito.wa2.g12.machinemock.dto.TransitDTO
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.util.*
import javax.crypto.SecretKey
import javax.imageio.ImageIO

data class TransitBody(val ticket_id : Long)

@RestController
class MachineController(private val secretKey: SecretKey) {

    private val jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build()
    @PostMapping("/machine")
    fun decodeQr(@RequestBody body : String): Mono<Any> {
        return mono {
           val login_jwt : String= WebClient.create("http://localhost:8081")
                .post()
                .uri("/user/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(LoginDTO("machine","machine")))
                .retrieve()
                .awaitBody()
            val image = Base64.getDecoder().decode(body)
            val byteArrayInputStream = ByteArrayInputStream(image)
            val bufferedImage = ImageIO.read(byteArrayInputStream)
            val bufferedImageLuminanceSource = BufferedImageLuminanceSource(bufferedImage)
            val hybridBinarizer = HybridBinarizer(bufferedImageLuminanceSource)
            val binaryBitmap = BinaryBitmap(hybridBinarizer)
            val multiFormatReader = MultiFormatReader()
            val token = multiFormatReader.decode(binaryBitmap).text
            println(token)
            try {
                if (token.trim().isEmpty() )
                    throw Exception()
                val jws = jwtParser.parseClaimsJws(token)
                val now = Calendar.getInstance().time
                if (now > jws.body.expiration)
                    throw Exception()
            }
            catch (e:Exception) {
                "jwt is not valid"
            }
            val jws = jwtParser.parseClaimsJws(token)
            val ticket_id: Long= jws.body.subject.toLong()
            println("Bearer "+login_jwt)
            val res:TransitDTO = WebClient.create("http://localhost:8087")
                .post()
                .uri("/transits")
                .header("Authorization","Bearer "+login_jwt)
                .accept()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(TransitBody(ticket_id)))
                .retrieve()
                .awaitBody()
            "ticket validiated and transit inserted"
        }

    }



}