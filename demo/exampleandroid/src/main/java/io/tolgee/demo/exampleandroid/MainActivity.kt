package io.tolgee.demo.exampleandroid

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import io.tolgee.Tolgee
import io.tolgee.TolgeeContextWrapper
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

  val tolgee = Tolgee.instance

  override fun attachBaseContext(newBase: Context?) {
    super.attachBaseContext(TolgeeContextWrapper.wrap(newBase))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launch {
      tolgee.changeFlow.collect {
        recreate()
      }
    }

    setContentView(R.layout.activity_main)

    setTitle(R.string.app_name)

    val name = findViewById<TextView>(R.id.app_name_text)
    val basic = findViewById<TextView>(R.id.basic_text)
    val parameter = findViewById<TextView>(R.id.parameterized_text)
    val plural = findViewById<TextView>(R.id.plural_text)
    val array = findViewById<TextView>(R.id.array_text)
    val buttonEn = findViewById<Button>(R.id.button_en)
    val buttonFr = findViewById<Button>(R.id.button_fr)
    val buttonCs = findViewById<Button>(R.id.button_cs)

    name.text = getString(R.string.app_name)
    basic.text = getString(R.string.description)
    parameter.text = getString(R.string.percentage_placeholder, "87")
    plural.text = resources.getQuantityString(R.plurals.plr_test_placeholder_2,3, 2, 3, "Plurals")
    array.text = resources.getStringArray(R.array.array_test).joinToString()

    buttonEn.setOnClickListener {
      tolgee.setLocale(Locale.ENGLISH)
      Tolgee.instance.preload(this)
    }
    buttonFr.setOnClickListener {
      tolgee.setLocale(Locale.FRENCH)
      Tolgee.instance.preload(this)
    }
    buttonCs.setOnClickListener {
      tolgee.setLocale(Locale("cs"))
      Tolgee.instance.preload(this)
    }
  }

  override fun onStart() {
    super.onStart()
    Tolgee.instance.preload(this)
  }
}