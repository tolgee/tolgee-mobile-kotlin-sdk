package dev.datlag.tolgee.extension

import dev.datlag.tolgee.cli.PathAware
import dev.datlag.tolgee.common.androidResources
import dev.datlag.tolgee.common.lazyMap
import dev.datlag.tolgee.model.Format
import dev.datlag.tolgee.model.pull.State
import dev.datlag.tooling.existsSafely
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty

open class PullExtension(objectFactory: ObjectFactory) : BaseTolgeeExtension(objectFactory) {

    open val path: DirectoryProperty = objectFactory.directoryProperty()

    open val languages: SetProperty<String> = objectFactory.setProperty(String::class.java)

    open val states: SetProperty<State> = objectFactory.setProperty(State::class.java)

    open val namespaces: SetProperty<String> = objectFactory.setProperty(String::class.java)

    open val tags: SetProperty<String> = objectFactory.setProperty(String::class.java)

    open val excludeTags: SetProperty<String> = objectFactory.setProperty(String::class.java)

    override fun setupConvention(project: Project, inherit: BaseTolgeeExtension?) {
        super.setupConvention(project, inherit)

        path.convention(project.provider {
            val configPath = configuration.orNull?.pull?.path

            configPath?.let {
                project.layout.projectDirectory.dir(configPath)
            }?.let {
                if (it.asFile.existsSafely()) {
                    it
                } else {
                    null
                }
            } ?: configPath?.let {
                project.rootProject.layout.projectDirectory.dir(configPath)
            }?.let {
                if (it.asFile.existsSafely()) {
                    it
                } else {
                    null
                }
            } ?: when (format.orNull) {
                is Format.ComposeXML -> project.layout.projectDirectory.dir(COMMON_RESOURCES_PATH)
                is Format.AndroidXML -> project.androidResources.filter { res ->
                    res.existsSafely()
                }.ifEmpty { project.androidResources }.firstOrNull()?.let { res ->
                    project.layout.projectDirectory.dir(res.path)
                }
                else -> null
            }
        })
        languages.convention(configuration.lazyMap(project = project, map = { it?.pull?.languages }) { null })
        states.convention(configuration.lazyMap(project = project, map = { it?.pull?.states }) { null })
        namespaces.convention(configuration.lazyMap(project = project, map = { it?.pull?.namespaces }) { null })
        tags.convention(configuration.lazyMap(project = project, map = { it?.pull?.tags }) { null })
        excludeTags.convention(configuration.lazyMap(project = project, map = { it?.pull?.excludeTags }) { null })
    }

    internal companion object : PathAware() {
        internal val COMMON_RESOURCES_PATH by lazy {
            "src${filePathDelimiter}commonMain${filePathDelimiter}composeResources"
        }
    }
}