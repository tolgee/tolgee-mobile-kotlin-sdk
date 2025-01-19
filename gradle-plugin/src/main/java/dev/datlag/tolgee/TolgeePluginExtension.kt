package dev.datlag.tolgee

import dev.datlag.tolgee.common.androidResources
import dev.datlag.tolgee.common.isAndroidOnly
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

    open val pullType: Property<PullType> = objectFactory.property(PullType::class.java)

    open val pullDestination: DirectoryProperty = objectFactory.directoryProperty()

    fun setupConvention(project: Project) {
        baseUrl.convention(DEFAULT_URL)
        apiKey.convention(project.provider {
            project.findProperty("tolgee.apikey")?.toString()?.ifBlank { null }
        })
        pullType.convention(project.provider {
            if (project.isAndroidOnly) {
                PullType.AndroidXML
            } else {
                PullType.ComposeXML
            }
        })
        pullDestination.convention(pullType.map {
            when (it) {
                is PullType.ComposeXML -> project.layout.projectDirectory.dir(COMMON_RESOURCES_PATH)
                is PullType.AndroidXML -> project.androidResources.firstOrNull()?.let { res ->
                    project.layout.projectDirectory.dir(res.path)
                }
                else -> null
            }
        })
    }

    sealed interface FilterState {
        val value: String

        object Untranslated : FilterState {
            override val value: String = "UNTRANSLATED"
        }

        object Translated : FilterState {
            override val value: String = "TRANSLATED"
        }

        object Reviewed : FilterState {
            override val value: String = "REVIEWED"
        }

        object Disabled : FilterState {
            override val value: String = "DISABLED"
        }
    }

    sealed interface PullType {
        val value: String

        object ComposeXML : PullType {
            override val value: String = "COMPOSE_XML"
        }

        object AndroidXML : PullType {
            override val value: String = "ANDROID_XML"
        }

        object Po : PullType {
            override val value: String = "PO"
        }

        data class Custom(override val value: String) : PullType
    }

    companion object {
        internal const val DEFAULT_URL = "https://app.tolgee.io/v2/"

        internal const val COMMON_RESOURCES_PATH = "src/commonMain/composeResources"
    }
}