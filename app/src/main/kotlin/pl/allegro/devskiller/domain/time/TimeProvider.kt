package pl.allegro.devskiller.domain.time

import java.time.Instant

interface TimeProvider {
    fun getTime(): Instant
}

class NowTimeProvider : TimeProvider {
    override fun getTime(): Instant = Instant.now()
}
