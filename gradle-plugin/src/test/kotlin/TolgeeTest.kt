import io.tolgee.TolgeePluginExtension
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertTrue

class TolgeeTest {
    @Test
    fun `applying the plugin registers the tolgee extension with conventions`() {
        val project = ProjectBuilder.builder().build()

        project.pluginManager.apply("io.tolgee.mobile-kotlin-sdk")

        val extension = project.extensions.getByName("tolgee") as TolgeePluginExtension
        assertTrue(extension.compilerPlugin.android.replaceGetString.get())
        assertTrue(extension.compilerPlugin.compose.replaceStringResource.get())
    }
}
