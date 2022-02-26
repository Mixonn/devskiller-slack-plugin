package pl.allegro.devskiller.domain.time

import java.time.Instant

class FixedTimeProvider(private val time: Instant) : TimeProvider {
    override fun getTime(): Instant = time
}
