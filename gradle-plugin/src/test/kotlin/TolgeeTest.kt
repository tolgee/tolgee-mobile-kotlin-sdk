import dev.datlag.tolgee.cli.TolgeeCLI
import kotlin.test.Test
import kotlin.test.assertNotNull

class TolgeeTest {

    @Test
    fun `tolgee cli version check`() {
        assertNotNull(TolgeeCLI.version(), "TolgeeCLI not installed")
    }

}