package io.tolgee.demo.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import compose_tolgee.demo.multiplatform_compose.generated.resources.Res
import compose_tolgee.demo.multiplatform_compose.generated.resources.description
import compose_tolgee.demo.multiplatform_compose.generated.resources.percentage_placeholder
import io.tolgee.Tolgee
import io.tolgee.stringResource
// import org.jetbrains.compose.resources.stringResource

@Composable
fun App() {
    // no remember required, using a singleton
    val tolgee = Tolgee.instance

    MaterialTheme {
        Column {
            Text(text = stringResource(Res.string.description))
            Text(text = stringResource(Res.string.percentage_placeholder))
            Button(
                onClick = {
                    tolgee.setLocale("en-US")
                }
            ) {
                Text(text = "English")
            }
        }
    }
}
