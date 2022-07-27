package it.polito.wa2.g12.machinemock.controller

import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import io.jsonwebtoken.Jwts
import it.polito.wa2.g12.machinemock.dto.LoginDTO
import it.polito.wa2.g12.machinemock.dto.TransitDTO
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.util.*
import javax.crypto.SecretKey
import javax.imageio.ImageIO

data class TransitBody(val ticket_id : Long,val ticket_type:String,val user : String)

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
            val err = try {
                if (token.trim().isEmpty() )
                    throw Exception()
                val jws = jwtParser.parseClaimsJws(token)
                val now = Calendar.getInstance().time
                val ext = jws.body.expiration
                ext.time = ext.time/1000
                if (now.time > ext.time)
                    throw Exception()
                false
            }
            catch (e:Exception) {
                true
            }
            if(err)
                "jwt is invalid"
            else {
                val jws = jwtParser.parseClaimsJws(token)
                val ticket_id: Long= jws.body.subject.toLong()
                val type : String = jws.body["type"].toString()
                val user : String = jws.body["user"].toString()
                val res:TransitDTO = WebClient.create("http://localhost:8087")
                    .post()
                    .uri("/transits")
                    .header("Authorization","Bearer "+login_jwt)
                    .accept()
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(TransitBody(ticket_id,type,user)))
                    .retrieve()
                    .awaitBody()
               "jwt is valid"
            }
        }

    }
}