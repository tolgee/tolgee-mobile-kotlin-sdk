package dev.datlag.tolgee.format

import android.os.Build
import kotlinx.datetime.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

@Throws(IllegalArgumentException::class)
internal actual fun Any.convertToInstant(): Instant = when (this) {
    is Date -> Instant.fromEpochMilliseconds(this.time)
    else -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when (this) {
                is java.time.LocalDateTime -> Instant.fromEpochSeconds(
                    this.atZone(ZoneId.systemDefault()).toEpochSecond()
                )
                is ZonedDateTime -> Instant.fromEpochSeconds(this.toEpochSecond())
                is java.time.Instant -> Instant.fromEpochMilliseconds(this.toEpochMilli())
                else -> throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
            }
        } else {
            throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
        }
    }
}