package it.polito.wa2.g12.loginservice.interceptor

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.ConsumptionProbe
import io.github.bucket4j.Refill
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.time.Duration
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RateLimiterInterceptor : HandlerInterceptorAdapter() {
    private val refill = Refill.intervally(10, Duration.ofSeconds(1))

    // Limit 10 req per second
    private val limit = Bandwidth.classic(10, refill)

    // construct the bucket
    private val bucket: Bucket = Bucket.builder().addLimit(limit).build()
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val probe: ConsumptionProbe = bucket.tryConsumeAndReturnRemaining(1)
        return if (probe.isConsumed) {
            response.addHeader("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
            true
        } else {
            val waitForRefill = probe.nanosToWaitForRefill / 1000000000
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", waitForRefill.toString())
            response.sendError(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "You have exhausted your API Request Quota"
            )
            false
        }
    }
}