package dev.datlag.tolgee.model

import de.comahe.i18n4k.messages.NameToIndexMapper
import de.comahe.i18n4k.messages.formatter.MessageParameters
import de.comahe.i18n4k.messages.formatter.MessageParametersEmpty
import de.comahe.i18n4k.messages.formatter.MessageParametersList
import de.comahe.i18n4k.messages.formatter.MessageParametersMap
import kotlinx.collections.immutable.toImmutableList

sealed interface TolgeeMessageParams : MessageParameters {
    override operator fun get(name: CharSequence): Any?

    data object None : TolgeeMessageParams {

        private val emptyParameters = MessageParametersEmpty

        override operator fun get(name: CharSequence): Any? = emptyParameters[name]
    }

    data class Indexed(
        val formatArgs: Collection<Any>
    ) : TolgeeMessageParams {

        constructor(vararg formatArgs: Any) : this(formatArgs.toImmutableList())

        private val listParameters = MessageParametersList(
            parameters = formatArgs.toList(),
            nameMapper = object : NameToIndexMapper {
                override fun getNameIndex(name: CharSequence): Int {
                    if (name.isEmpty()) {
                        throw IllegalArgumentException("Parameter name must not be empty")
                    }
                    return name.toString().toIntOrNull() ?: throw IllegalArgumentException("Parameter name must be an int, got: $name")
                }
            }
        )

        override operator fun get(name: CharSequence): Any? = listParameters[name]
    }

    data class Mapped(
        val formatArgs: Map<String, Any>
    ) : TolgeeMessageParams {

        private val mapParameters = MessageParametersMap(formatArgs)

        override operator fun get(name: CharSequence): Any? = mapParameters[name]
    }
}