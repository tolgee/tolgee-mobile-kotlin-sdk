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
            // Use tolgee version of stringResource composable
            // (or alternatively enable tolgee compiler plugin which will convert the calls automatically)
            Text(text = stringResource(Res.string.description))
            Text(text = stringResource(Res.string.percentage_placeholder, "87"))
            Button(
                onClick = {
                    tolgee.setLocale("en")
                }
            ) {
                Text(text = "English")
            }
            Button(
                onClick = {
                    tolgee.setLocale("fr")
                }
            ) {
                Text(text = "Français")
            }
            Button(
                onClick = {
                    tolgee.setLocale("cs")
                }
            ) {
                Text(text = "Čeština")
            }
        }
    }
}
