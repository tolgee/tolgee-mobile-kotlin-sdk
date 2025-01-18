package dev.datlag.tolgee

import org.gradle.api.Project
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

    fun setupConvention(project: Project) {
        baseUrl.convention(DEFAULT_URL)
        apiKey.convention(project.provider {
            project.findProperty("tolgee.apikey")?.toString()?.ifBlank { null }
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

    companion object {
        internal const val DEFAULT_URL = "https://app.tolgee.io/v2/"
    }
}