package dev.datlag.tolgee

import dev.datlag.tolgee.common.androidResources
import dev.datlag.tolgee.common.isAndroidOnly
import dev.datlag.tooling.existsSafely
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

open class TolgeePluginExtension @Inject constructor(objectFactory: ObjectFactory) {

    open val baseUrl: Property<String> = objectFactory.property(String::class.java)

    open val projectId: Property<String> = objectFactory.property(String::class.java)

    open val apiKey: Property<String> = objectFactory.property(String::class.java)

    open val languages: SetProperty<String> = objectFactory.setProperty(String::class.java)

    open val filterState: SetProperty<FilterState> = objectFactory.setProperty(FilterState::class.java)

    open val type: Property<PullType> = objectFactory.property(PullType::class.java)

    open val destination: DirectoryProperty = objectFactory.directoryProperty()

    fun setupConvention(project: Project) {
        baseUrl.convention(DEFAULT_URL)
        apiKey.convention(project.provider {
            project.findProperty("tolgee.apikey")?.toString()?.ifBlank { null }
                ?: project.findProperty("tolgee.apiKey")?.toString()?.ifBlank { null }
        })
        type.convention(project.provider {
            if (project.isAndroidOnly) {
                PullType.AndroidXML
            } else {
                PullType.ComposeXML
            }
        })
        destination.convention(type.map {
            when (it) {
                is PullType.ComposeXML -> project.layout.projectDirectory.dir(COMMON_RESOURCES_PATH)
                is PullType.AndroidXML -> project.androidResources.filter { res ->
                    res.existsSafely()
                }.ifEmpty { project.androidResources }.firstOrNull()?.let { res ->
                    project.layout.projectDirectory.dir(res.path)
                }
                else -> null
            }
        })
    }

    @JvmDefaultWithCompatibility
    sealed interface FilterState : CharSequence {
        val value: String

        override val length: Int
            get() = value.length

        override operator fun get(index: Int): Char {
            return value[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return value.subSequence(startIndex, endIndex)
        }

        object Untranslated : FilterState {
            override val value: String = "UNTRANSLATED"

            override fun toString(): String = value
        }

        object Translated : FilterState {
            override val value: String = "TRANSLATED"

            override fun toString(): String = value
        }

        object Reviewed : FilterState {
            override val value: String = "REVIEWED"

            override fun toString(): String = value
        }

        object Disabled : FilterState {
            override val value: String = "DISABLED"

            override fun toString(): String = value
        }

        @JvmInline
        value class Custom(override val value: String) : FilterState {
            override fun toString(): String = value
        }
    }

    @JvmDefaultWithCompatibility
    sealed interface PullType : CharSequence {
        val value: String

        override val length: Int
            get() = value.length

        override operator fun get(index: Int): Char {
            return value[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return value.subSequence(startIndex, endIndex)
        }

        object ComposeXML : PullType {
            override val value: String = "COMPOSE_XML"

            override fun toString(): String = value
        }

        object AndroidXML : PullType {
            override val value: String = "ANDROID_XML"

            override fun toString(): String = value
        }

        object Po : PullType {
            override val value: String = "PO"

            override fun toString(): String = value
        }

        @JvmInline
        value class Custom(override val value: String) : PullType {
            override fun toString(): String = value
        }
    }

    companion object {
        internal const val DEFAULT_URL = "https://app.tolgee.io/v2/"

        internal const val COMMON_RESOURCES_PATH = "src/commonMain/composeResources"
    }
}