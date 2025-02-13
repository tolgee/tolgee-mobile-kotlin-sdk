package dev.datlag.tolgee.format

import dev.datlag.tolgee.common.sprintf
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

/**
 * Mostly copied from https://github.com/sergeych/mp_stools
 */
@Serializable
internal sealed interface Positioning {

    @Serializable
    data object Left : Positioning

    @Serializable
    data object Right : Positioning

    @Serializable
    data object Center : Positioning
}

/**
 * Mostly copied from https://github.com/sergeych/mp_stools
 */
internal data class Specification(
    val parent: Sprintf,
    var index: Int
) {

    private var stage: Stage = Stage.Flags

    private var size: Int = -1
    private var fractionalPartSize: Int = -1
    private var positioning: Positioning = Positioning.Right
    private var fillChar = ' '
    private var currentPart = StringBuilder()

    private var explicitPlus = false
    private var done = false
    private var indexIsOverridden = false

    private val isScanningFlags: Boolean
        get() = stage is Stage.Flags

    private val time: LocalDateTime
        get() = parent.getLocalDateTime(index)

    internal fun scan() {
        while (!done) {
            when (val ch = parent.nextChar()) {
                '-', '^' -> {
                    if (!isScanningFlags) {
                        invalidFormat("unexpected $ch")
                    }
                    positioning = if (ch == '-') Positioning.Left else Positioning.Center
                }
                '+' -> {
                    if (!isScanningFlags) {
                        invalidFormat("unexpected $ch")
                    }
                    explicitPlus = true
                }
                in "*#_=" -> {
                    if (!isScanningFlags) {
                        invalidFormat("bad fill char $ch position")
                    }
                    fillChar = ch
                }
                '0' -> {
                    if (isScanningFlags) {
                        fillChar = '0'
                    } else {
                        currentPart.append(ch)
                    }
                }
                in "123456789" -> {
                    if (stage is Stage.Flags) {
                        stage = Stage.Length
                    }
                    currentPart.append(ch)
                }
                '$', '!' -> {
                    if (stage !is Stage.Length) {
                        invalidFormat("unexpected $ch position")
                    }
                    if (indexIsOverridden) {
                        invalidFormat("argument number '$ch' should occur only once")
                    }
                    indexIsOverridden = true
                    index = currentPart.toString().toInt() - 1
                    parent.pushbackArgumentIndex()
                    currentPart.clear()
                }
                's' -> createStringField()
                'd', 'i' -> createIntegerField()
                'o' -> createOctalField()
                'x' -> createHexField(false)
                'X' -> createHexField(true)
                'f', 'F' -> createFloatField()
                'e' -> createScientificField(false)
                'E' -> createScientificField(true)
                'g' -> createAutoFloat(false)
                'G' -> createAutoFloat(true)
                'c', 'C' -> createCharacter()
                't' -> createTimeField(false)
                'T' -> createTimeField(true)
                '.' -> {
                    when (stage) {
                        is Stage.Flags -> {
                            stage = Stage.Fraction
                        }
                        is Stage.Length -> {
                            endStage(false)
                            stage = Stage.Fraction
                        }
                        else -> invalidFormat("can not parse specification: unexpected '.'")
                    }
                }
                else -> invalidFormat("unexpected character '$ch'")
            }
        }
    }

    private fun invalidFormat(reason: String): Nothing = parent.invalidFormat(reason)

    private fun createStringField() {
        endStage()
        insertField(parent.getText(index))
    }

    private fun createIntegerField() {
        endStage()
        val number = parent.getNumber(index).toLong()

        if (number < 0 && fillChar == '0') {
            insertField((-number).toString(), "-")
        } else {
            if (explicitPlus && fillChar == '0' && number > 0) {
                insertField(number.toString(), "+")
            } else {
                insertField(if (explicitPlus && number > 0) "+$number" else "$number")
            }
        }
    }

    private fun createOctalField() {
        endStage()
        val number = parent.getNumber(index).toLong()

        if (explicitPlus) {
            invalidFormat("'+' is incompatible with oct format")
        }
        insertField(number.toString(8))
    }

    private fun createHexField(upperCase: Boolean) {
        endStage()

        if (explicitPlus) {
            invalidFormat("'+' is incompatible with hex format")
        }

        val src = parent.notNull(index)
        val n = (src as? Number)?.toLong() ?: invalidFormat("can not treat '$src' as integer number")
        val text = if (n >= 0) {
            n.toString(16)
        } else {
            val n2 = n.toULong()
            val t = when(src) {
                is Byte -> (n2 and 0xFFu).toString(16)
                is Short -> (n2 and 0xFFFFu).toString(16)
                is Int -> (n2 and 0xFFFFFFFFu).toString(16)
                else -> n2.toString(16)
            }
            if (size >=- 0) {
                t.take(size)
            } else {
                t
            }
        }
        insertField(if (upperCase) text.uppercase() else text.lowercase())
    }

    private fun createFloatField() {
        endStage()
        val number = parent.getNumber(index).toDouble()
        val t = ExponentFormatter.fractionalFormat(number, size, fractionalPartSize)

        if (explicitPlus && fillChar == '0' && number > 0) {
            insertField(t, "+")
        } else {
            insertField(if (explicitPlus && number > 0) "+$t" else t)
        }
    }

    private fun createScientificField(upperCase: Boolean) {
        endStage()
        val number = parent.getNumber(index).toDouble()
        val t = scientificFormat(number, size, fractionalPartSize).let {
            if (upperCase) it.uppercase() else it.lowercase()
        }

        if (explicitPlus && fillChar == '0' && number > 0) {
            insertField(t, "+")
        } else {
            insertField(if (explicitPlus && number > 0) "+$t" else t)
        }
    }

    private fun createAutoFloat(upperCase: Boolean) {
        endStage()
        val number = parent.getNumber(index)
        val t = number.toString().let {
            if (upperCase) it.uppercase() else it.lowercase()
        }

        if (explicitPlus && fillChar == '0' && number.toDouble() > 0) {
            insertField(t, "+")
        } else {
            insertField(if (explicitPlus && number.toFloat() > 0) "+$t" else t)
        }
    }

    private fun createCharacter() {
        endStage()
        insertField(parent.getCharacter(index).toString())
    }

    private fun createTimeField(upperCase: Boolean) {
        val ch = parent.nextChar()
        endStage()

        val result: String = when (ch) {
            'H' -> "%02d".sprintf(time)
            'k' -> "%d".sprintf(time.hour)
            'I', 'l' -> {
                var t = time.hour
                if (t > 12) {
                    t -= 12
                }
                if (ch == 'I') {
                    "%02d".sprintf(t)
                } else {
                    t.toString()
                }
            }
            'M' -> "%02d".sprintf(time.minute)
            'S' -> "%02d".sprintf(time.second)
            'L' -> "%03d".sprintf(time.nanosecond / 1_000_000)
            'N' -> "%09d".sprintf(time.nanosecond)
            'z' -> {
                val tz = TimeZone.currentSystemDefault()
                tz.offsetAt(time.toInstant(tz)).toString()
            }
            's' -> {
                val tz = TimeZone.currentSystemDefault()
                time.toInstant(tz).epochSeconds.toString()
            }
            'Q' -> {
                val tz = TimeZone.currentSystemDefault()
                time.toInstant(tz).toEpochMilliseconds().toString()
            }
            'e' -> time.dayOfMonth.toString()
            'd' -> "%02s".sprintf(time.dayOfMonth)
            'm' -> "%02s".sprintf(time.month.number)
            'y' -> time.year.toString().takeLast(2)
            'Y' -> "%04d".sprintf(time.year)
            'j' -> "%03d".sprintf(time.dayOfYear)
            else -> invalidFormat("unknown time field specification: 't$ch'")
        }
        insertField(result)
    }

    private fun endStage(setDone: Boolean = true) {
        if (setDone) {
            done = true
        }
        if (currentPart.isNotEmpty()) {
            when (stage) {
                is Stage.Length -> {
                    size = currentPart.toString().toInt()
                }
                is Stage.Fraction -> {
                    fractionalPartSize = currentPart.toString().toInt()
                }
                is Stage.Flags -> invalidFormat("can not parse format specifier (error 7)")
            }
            currentPart.clear()
        }
    }

    private fun insertField(text: String, prefix: String = "") {
        val l = text.length + prefix.length
        if (size < 0 || size < l) {
            parent.specificationDone(prefix + text)
        } else {
            var padStart = 0
            var padEnd = 0
            when (positioning) {
                is Positioning.Left -> {
                    padEnd = size - l
                }
                is Positioning.Right -> {
                    padStart = size - l
                }
                is Positioning.Center -> {
                    padStart = (size - l) / 2
                    padEnd = size - padStart - l
                }
            }
            val result = StringBuilder(prefix)
            while (padStart-- > 0) {
                result.append(fillChar)
            }
            result.append(text)
            while (padEnd-- > 0) {
                result.append(fillChar)
            }
            parent.specificationDone(result.toString())
        }
    }

    private fun scientificFormat(value: Double, width: Int, fractionPartLength: Int = -1) = ExponentFormatter(value).scientific(width, fractionPartLength)

    @Serializable
    sealed interface Stage {

        @Serializable
        data object Flags : Stage

        @Serializable
        data object Length : Stage

        @Serializable
        data object Fraction : Stage
    }
}