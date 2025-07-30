package io.tolgee.demo.examplejetpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import io.tolgee.Tolgee
import io.tolgee.demo.examplejetpack.ui.theme.ComposetolgeeTheme
import kotlinx.coroutines.launch
import de.comahe.i18n4k.forLocaleTag
import kotlinx.coroutines.flow.mapLatest
import io.tolgee.stringResource
import io.tolgee.pluralStringResource

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launch {
      // Keep the Activity title updated
      Tolgee.instance.tFlow(this@MainActivity, R.string.app_name).collect { title = it }
    }

    enableEdgeToEdge()
    setContent {
      ComposetolgeeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Column(modifier = Modifier.padding(innerPadding)) {
            BasicText()
            ParametrizedText(name = "Android")
            PluralText(
              count = 10,
              param = 10,
            )
            LocaleSwitcher()
          }
        }
      }
    }
  }
}

@Composable
fun BasicText(modifier: Modifier = Modifier) {
  // Use tolgee version of stringResource composable
  Text(
    text = stringResource(R.string.description),
    modifier = modifier
  )
}

@Composable
fun ParametrizedText(name: String, modifier: Modifier = Modifier) {
  // Passing parameters for the stringResource is supported
  Text(
    text = stringResource(R.string.percentage_placeholder, name),
    modifier = modifier
  )
}

@Composable
fun PluralText(count: Int, param: Int, modifier: Modifier = Modifier) {
  // Plurals are also supported
  Text(
    text = pluralStringResource(R.plurals.plr_test_placeholder_2, count, param, "Plurals"),
    modifier = modifier
  )
}

val LOCALES = arrayOf(
  "en" to "English",
  "fr" to "Français",
  "cs" to "Čeština",
)

@Composable
fun LocaleSwitcher(modifier: Modifier = Modifier) {
  SingleChoiceSegmentedButtonRow(modifier = modifier) {
    LOCALES.forEachIndexed { index, (locale, name) ->
      ChangeLocaleButton(
        index = index,
        count = LOCALES.size,
        locale = locale,
        name = name,
      )
    }
  }
}

fun Tolgee.isLocaleSelected(locale: String, fallback: Boolean): Boolean {
  return getLocale() == forLocaleTag(locale) || (fallback && LOCALES.none { (locale, _) ->
    getLocale() == forLocaleTag(locale)
  })
}

@Composable
fun isLocaleSelected(locale: String, fallback: Boolean): Boolean {
  val tolgee = Tolgee.instance
  return tolgee.changeFlow.mapLatest {
    tolgee.isLocaleSelected(locale, fallback)
  }.collectAsState(initial = tolgee.isLocaleSelected(locale, fallback)).value
}

@Composable
fun SingleChoiceSegmentedButtonRowScope.ChangeLocaleButton(index: Int, count: Int, locale: String, name: String) {
  SegmentedButton(
    shape = SegmentedButtonDefaults.itemShape(
      index = index,
      count = count
    ),
    onClick = {
      Tolgee.instance.setLocale(locale)
    },
    selected = isLocaleSelected(locale, index == 0),
    label = { Text(text = name) }
  )
}

@Preview(showBackground = true)
@Composable
fun ParametrizedTextPreview() {
  ComposetolgeeTheme {
    ParametrizedText("Android")
  }
}