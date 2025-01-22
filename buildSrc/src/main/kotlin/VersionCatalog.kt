import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.api.artifacts.VersionCatalog as VCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension as VCatalogEx

class VersionCatalog(project: Project) {
    private val libs: VCatalog = project.extensions.getByType<VCatalogEx>().named("libs")

    private fun version(key: String): String = libs.findVersion(key).get().requiredVersion
    private fun versionInt(key: String): Int = version(key).getDigitsOrNull()?.toIntOrNull() ?: version(key).toInt()

    private fun String.getDigitsOrNull(): String? {
        val replaced = this.replace("\\D+".toRegex(), String())
        return replaced.ifBlank {
            null
        }
    }

    val libVersion: String
        get() = version("library")

    val libVersionCode: Int
        get() = versionInt("library")
}

val Project.libVersion: String
    get() = VersionCatalog(this).libVersion

val Project.libVersionCode: Int
    get() = VersionCatalog(this).libVersionCode