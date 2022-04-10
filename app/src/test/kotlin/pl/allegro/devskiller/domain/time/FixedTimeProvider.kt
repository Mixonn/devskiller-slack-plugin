package pl.allegro.devskiller.domain.time

import java.time.Duration
import java.time.Instant

class FixedTimeProvider(private val time: Instant = now) : TimeProvider {
    override fun getTime(): Instant = time

    companion object {
        val now = Instant.parse("2022-01-23T10:23:00.000Z")
        val overTwoHoursAgo = now.minus(Duration.ofMinutes(121))
        val twoHoursAgo = now.minus(Duration.ofHours(2))
        val almostTwoHoursAgo = now.minus(Duration.ofMinutes(119))
        val twoDaysAgo = now.minus(Duration.ofDays(2))
    }
}
