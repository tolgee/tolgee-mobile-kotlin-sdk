package dev.datlag.tolgee.format

import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong

internal data class ExponentFormatter(val value: Double) {

    var mantissa: Double
        private set

    var exponent: Int = 0
        private set(value) {
            field = value
            strExponent = "e$exponent"
        }

    private val mstr: String
    private var strExponent: String

    init {
        val x = abs(value)

        exponent = log10(x).toInt()
        if (exponent < 0) {
            exponent--
        }

        mantissa = x / 10.0.pow(exponent)
        if (value < 0) {
            mantissa = -mantissa
        }

        mstr = mantissa.toString()
        strExponent = "e$exponent"
    }

    fun scientific(width: Int, fractionWidth: Int = -1): String {
        val minLength = if (mantissa < 0) 2 else 1

        fun mpart(length: Int): String {
            var l = length
            if (l > mstr.length) {
                l = mstr.length
            }

            val result = StringBuilder(mstr.slice(0 until l))
            if (result.length == mstr.length) {
                return result.toString()
            }

            var nextDigit = mstr[result.length]
            if (nextDigit == '.') {
                if (result.length + 1 >= mstr.length) {
                    return result.toString()
                }
                nextDigit = mstr[result.length + 1]
            }
            if (nextDigit in "56789") {
                val (m, ovf) = roundUp(result)
                if (!ovf) {
                    return m
                }

                exponent++

                val pointPos = m.indexOf('.')
                val mb = StringBuilder(m)
                if (pointPos == -1) {
                    return m
                }

                return mb.deleteAt(pointPos).insert(pointPos - 1, '.').toString()
            }
            return result.toString()
        }

        if (width == 0) {
            return mstr + strExponent
        }
        if (fractionWidth < 0 && width > 0) {
            var l = width - strExponent.length
            if (l < minLength) {
                l = minLength
            }
            return mpart(l) + strExponent
        }

        if (fractionWidth < 0 && width < 0) {
            return mstr + strExponent
        }

        if (fractionWidth == 0) {
            return "${mstr[0]}$strExponent"
        }

        return mpart(minLength + 1 + fractionWidth) + strExponent
    }

    override fun toString(): String {
        return "${mantissa}e$exponent"
    }

    internal companion object {
        internal fun fractionalFormat(startValue: Double, width: Int, fractionPartLength: Int = -1): String {
            var value = startValue
            val result = StringBuilder()

            if (abs(value) >= 1) {
                val i = if (fractionPartLength == 0) {
                    value.roundToLong()
                } else {
                    value.toLong()
                }
                result.append(i)
                result.append('.')
                value -= i
            } else {
                result.append(if (value < 0) "-0." else "0.")
            }

            var fl = if (fractionPartLength < 0) {
                if (width < 0) 6 else width - result.length
            } else {
                fractionPartLength
            }
            var rest = value * 10

            while (fl-- > 0) {
                val d = rest.toInt()
                result.append(abs(d))
                rest = (rest - d) * 10
            }

            return if (rest.toInt().absoluteValue < 5) {
                result.toString()
            } else {
                roundUp(result, keepWidth = false).first
            }
        }

        private fun roundUp(
            result: StringBuilder,
            length: Int = result.length,
            pos: Int = result.length - 1,
            keepWidth: Boolean = true
        ): Pair<String, Boolean> {
            if (pos < 0) {
                result.insert(0, '1')
                if (keepWidth) {
                    result.deleteAt(length)
                }
                return result.toString() to true
            }

            val d = result[pos]
            if (d == '.') {
                return roundUp(result, length, pos - 1, keepWidth)
            }

            if (d != '9') {
                result[pos] = d + 1
                return result.toString() to false
            }

            result[pos] = '0'
            return roundUp(result, length, pos - 1, keepWidth)
        }
    }
}