package dev.datlag.tolgee.model

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import dev.datlag.tolgee.model.pull.State
import dev.datlag.tolgee.model.push.Mode
import dev.datlag.tooling.scopeCatching
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.InputStream

@Serializable
internal data class Configuration(
    @SerialName("apiUrl") private val _apiUrl: String? = null,
    @SerialName("projectId") private val _projectId: String? = null,
    @SerialName("apiKey") private val _apiKey: String? = null,
    @SerialName("format") private val _format: String? = null,
    @SerialName("pull") val pull: Pull? = null,
    @SerialName("push") val push: Push? = null,
) {
    @Transient
    val apiUrl: String? = _apiUrl?.ifBlank { null }?.trim()

    @Transient
    val projectId: String? = _projectId?.ifBlank { null }?.trim()

    @Transient
    val apiKey: String? = _apiKey?.ifBlank { null }?.trim()

    @Transient
    val format: Format? = _format?.ifBlank { null }?.trim()?.let(Format::from)

    /**
     * De-serialization may succeed even if it should not because all values are optional, resulting in an empty object.
     */
    fun isNull(): Boolean {
        val pullNull = pull == null || pull.isNull()
        val pushNull = push == null || push.isNull()

        return pullNull && pushNull && apiUrl.isNullOrBlank() && projectId.isNullOrBlank() && apiKey.isNullOrBlank() && format.isNullOrBlank()
    }

    fun toNullIfEmpty(): Configuration? {
        return if (isNull()) {
            null
        } else {
            this
        }
    }

    @Serializable
    internal data class Pull(
        @SerialName("path") private val _path: String? = null,
        @SerialName("languages") private val _languages: List<String> = emptyList(),
        @SerialName("namespaces") private val _namespaces: List<String> = emptyList(),
        @SerialName("states") private val _states: List<String> = emptyList(),
        @SerialName("tags") private val _tags: List<String> = emptyList(),
        @SerialName("excludeTags") private val _excludeTags: List<String> = emptyList(),
    ) {
        @Transient
        val path: String? = _path?.ifBlank { null }?.trim()

        @Transient
        val languages: List<String> = _languages.mapNotNull { it.ifBlank { null } }

        @Transient
        val namespaces: List<String> = _namespaces.mapNotNull { it.ifBlank { null } }

        @Transient
        val states: Set<State> = _states.filterNot { it.isBlank() }.map(State::from).toSet()

        @Transient
        val tags: List<String> = _tags.mapNotNull { it.ifBlank { null } }

        @Transient
        val excludeTags: List<String> = _excludeTags.mapNotNull { it.ifBlank { null } }

        /**
         * De-serialization may succeed even if it should not because all values are optional, resulting in an empty object.
         */
        fun isNull(): Boolean {
            return path.isNullOrBlank()
                    && languages.isEmpty()
                    && namespaces.isEmpty()
                    && states.isEmpty()
                    && tags.isEmpty()
                    && excludeTags.isEmpty()
        }
    }

    @Serializable
    internal data class Push(
        @SerialName("forceMode") private val _forceMode: String? = null,
        @SerialName("languages") private val _languages: List<String> = emptyList(),
        @SerialName("namespaces") private val _namespaces: List<String> = emptyList(),
    ) {
        @Transient
        val forceMode: Mode? = _forceMode?.ifBlank { null }?.trim()?.let(Mode::from)

        @Transient
        val languages: List<String> = _languages.mapNotNull { it.ifBlank { null } }

        @Transient
        val namespaces: List<String> = _namespaces.mapNotNull { it.ifBlank { null } }

        /**
         * De-serialization may succeed even if it should not because all values are optional, resulting in an empty object.
         */
        fun isNull(): Boolean {
            return forceMode.isNullOrBlank() && languages.isEmpty() && namespaces.isEmpty()
        }
    }

    companion object {
        private val jsonParser = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        private val yamlParser = Yaml.default

        fun from(data: String): Configuration? = scopeCatching {
            jsonParser.decodeFromString<Configuration>(data)
        }.getOrNull()?.toNullIfEmpty() ?: scopeCatching {
            yamlParser.decodeFromString<Configuration>(data)
        }.getOrNull()?.toNullIfEmpty()

        fun from(stream: InputStream): Configuration? = scopeCatching {
            jsonParser.decodeFromStream<Configuration>(stream)
        }.getOrNull()?.toNullIfEmpty() ?: scopeCatching {
            scopeCatching {
                stream.reset()
            }
            yamlParser.decodeFromStream<Configuration>(stream)
        }.getOrNull()?.toNullIfEmpty()

        fun from(file: File): Configuration? = file.inputStream().use(::from) ?: from(file.readText())
    }
}