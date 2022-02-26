package pl.allegro.devskiller.domain.time

import java.time.Instant

class NowTimeProvider : TimeProvider {
    override fun getTime(): Instant = Instant.now()
}
