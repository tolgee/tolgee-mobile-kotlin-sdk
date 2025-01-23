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

    @Serializable
    internal data class Pull(
        @SerialName("path") private val _path: String? = null,
        @SerialName("languages") val languages: List<String> = emptyList(),
        @SerialName("namespaces") val namespaces: List<String> = emptyList(),
        @SerialName("states") private val _states: List<String> = emptyList(),
    ) {
        @Transient
        val path: String? = _path?.ifBlank { null }?.trim()

        @Transient
        val states: Set<State> = _states.map(State::from).toSet()
    }

    @Serializable
    internal data class Push(
        @SerialName("forceMode") private val _forceMode: String? = null,
        @SerialName("languages") val languages: List<String> = emptyList(),
        @SerialName("namespaces") val namespaces: List<String> = emptyList(),
    ) {
        @Transient
        val forceMode: Mode? = _forceMode?.ifBlank { null }?.trim()?.let(Mode::from)
    }

    companion object {
        private val jsonParser = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        private val yamlParser = Yaml.default

        fun from(data: String): Configuration? = scopeCatching {
            jsonParser.decodeFromString<Configuration>(data)
        }.getOrNull() ?: scopeCatching {
            yamlParser.decodeFromString<Configuration>(data)
        }.getOrNull()

        fun from(stream: InputStream): Configuration? = scopeCatching {
            jsonParser.decodeFromStream<Configuration>(stream)
        }.getOrNull() ?: scopeCatching {
            scopeCatching {
                stream.reset()
            }
            yamlParser.decodeFromStream<Configuration>(stream)
        }.getOrNull()

        fun from(file: File): Configuration? = file.inputStream().use(::from) ?: from(file.readText())
    }
}