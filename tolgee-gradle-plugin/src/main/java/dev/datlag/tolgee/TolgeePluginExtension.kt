package dev.datlag.tolgee

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class TolgeePluginExtension @Inject constructor(objectFactory: ObjectFactory) {

    open val baseUrl: Property<String> = objectFactory.property(String::class.java)

    open val projectId: Property<String> = objectFactory.property(String::class.java)

    open val apiKey: Property<String> = objectFactory.property(String::class.java)

    open val languages: ListProperty<String> = objectFactory.listProperty(String::class.java)

    fun setupConvention(project: Project) {
        baseUrl.convention(DEFAULT_URL)
    }

    companion object {
        internal const val DEFAULT_URL = "https://app.tolgee.io/v2/"
    }
}