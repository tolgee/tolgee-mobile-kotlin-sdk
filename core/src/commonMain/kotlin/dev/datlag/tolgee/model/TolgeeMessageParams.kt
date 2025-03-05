package dev.datlag.tolgee.model

import de.comahe.i18n4k.messages.NameToIndexMapper
import de.comahe.i18n4k.messages.formatter.MessageParameters
import de.comahe.i18n4k.messages.formatter.MessageParametersEmpty
import de.comahe.i18n4k.messages.formatter.MessageParametersList
import de.comahe.i18n4k.messages.formatter.MessageParametersMap
import kotlinx.collections.immutable.toImmutableList

/**
 * Represents parameters for a message to be used in localization or formatting.
 * This sealed interface allows different types of parameter representations: none, indexed, or mapped.
 */
sealed interface TolgeeMessageParams : MessageParameters {
    /**
     * Retrieves the value associated with the given key.
     *
     * @param name the key to search for in the collection
     * @return the value associated with the key, or null if the key is not found
     */
    override operator fun get(name: CharSequence): Any?

    /**
     * Represents a "None" implementation of the TolgeeMessageParams interface.
     * This object is used as a placeholder or default implementation where no parameters are required.
     */
    data object None : TolgeeMessageParams {

        /**
         * A constant representing an empty implementation of message parameters.
         *
         * This is specifically used to represent the absence of any parameters
         * for message formatting or translation. Typically utilized as a placeholder
         * or default value when no dynamic parameters are needed.
         */
        private val emptyParameters = MessageParametersEmpty

        /**
         * Retrieves the value associated with the given key from the parameters.
         *
         * @param name the key for which the value is to be retrieved
         * @return the value associated with the key, or null if the key is not present
         */
        override operator fun get(name: CharSequence): Any? = emptyParameters[name]
    }

    /**
     * Represents indexed parameters used in formatting messages.
     *
     * This class is designed to handle indexed parameters for use in formatted strings.
     * It allows accessing the parameters by their position or name.
     *
     * @property argList A collection of arguments used for indexed formatting.
     */
    data class Indexed(
        val argList: List<Any>
    ) : TolgeeMessageParams {

        /**
         * Secondary constructor for the `Indexed` class.
         *
         * This constructor accepts a variable number of arguments (`formatArgs`) and processes them into
         * an immutable list which is passed to the primary constructor of the `Indexed` class.
         *
         * @param formatArgs The variable number of arguments to be used as message parameters.
         */
        constructor(vararg formatArgs: Any) : this(formatArgs.toImmutableList())

        /**
         * Represents a list-based implementation for message parameters, allowing the retrieval of arguments
         * by their index. It maps parameter names, which are expected to be numeric strings, to their
         * corresponding indices within the list.
         *
         * The `parameters` field is initialized from the given collection of formatting arguments, providing
         * sequential indexing for message parameters. A custom `NameToIndexMapper` is employed to map
         * parameter names to indices by converting the name to an integer.
         *
         * Throws:
         * - `IllegalArgumentException` if the parameter name is empty or cannot be converted to an integer.
         */
        private val listParameters = MessageParametersList(
            parameters = argList,
            nameMapper = object : NameToIndexMapper {
                /**
                 * Retrieves the index corresponding to the given name.
                 *
                 * This method interprets the provided `name` as an integer. If the name is empty
                 * or cannot be parsed as an integer, it throws an `IllegalArgumentException`.
                 *
                 * @param name The name to be mapped to an index. Must be a non-empty character sequence
                 *             that can be parsed as an integer.
                 * @return The index as an integer parsed from the given name.
                 * @throws IllegalArgumentException If `name` is empty or cannot be parsed as an integer.
                 */
                override fun getNameIndex(name: CharSequence): Int {
                    if (name.isEmpty()) {
                        throw IllegalArgumentException("Parameter name must not be empty")
                    }
                    return name.toString().toIntOrNull() ?: throw IllegalArgumentException("Parameter name must be an int, got: $name")
                }
            }
        )

        /**
         * Retrieves the value associated with the given name from the list of parameters.
         *
         * @param name the name of the parameter to retrieve
         * @return the value associated with the given name, or null if not found
         */
        override operator fun get(name: CharSequence): Any? = listParameters[name]
    }

    /**
     * Represents a mapped parameter structure, used for resolving messages by name.
     *
     * This class is part of the `TolgeeMessageParams` interface and allows mapping
     * of parameter names to their corresponding values. It is particularly useful in
     * contexts where message parameter resolution is performed based on string keys.
     *
     * @property formatArgs A map of parameter names to their corresponding values.
     *                       These are the arguments used to format a message.
     */
    data class Mapped(
        val formatArgs: Map<String, Any>
    ) : TolgeeMessageParams {

        /**
         * Maps provided format arguments into a structure useful for retrieving message parameters.
         *
         * This property utilizes a [MessageParametersMap] to enable retrieval of message parameters
         * by their name, which can be useful for localized string formatting.
         */
        private val mapParameters = MessageParametersMap(formatArgs)

        /**
         * Retrieves the value associated with the given parameter name from the map of parameters.
         *
         * @param name The name of the parameter to retrieve the value for.
         * @return The value associated with the provided parameter name, or null if no such value exists.
         */
        override operator fun get(name: CharSequence): Any? = mapParameters[name]
    }
}