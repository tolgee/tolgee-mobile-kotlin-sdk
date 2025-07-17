package io.tolgee.demo.view

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import io.tolgee.Tolgee
import io.tolgee.TolgeeContextWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : ComponentActivity() {

    val tolgee = Tolgee.instance

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(TolgeeContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val basic = findViewById<TextView>(R.id.basic_text)
        val name = findViewById<TextView>(R.id.app_name_text)
        val parameter = findViewById<TextView>(R.id.parameterized_text)
        val plural = findViewById<TextView>(R.id.plural_text)
        val array = findViewById<TextView>(R.id.array_text)
        val buttonEn = findViewById<Button>(R.id.button_en)
        val buttonFr = findViewById<Button>(R.id.button_fr)
        val buttonCs = findViewById<Button>(R.id.button_cs)

        lifecycleScope.launch {
            tolgee.tFlow(
                context = this@MainActivity,
                id = R.string.app_name,
            ).collect { value ->
                withContext(Dispatchers.Main) {
                    name.text = value
                }
            }
        }
        lifecycleScope.launch {
            tolgee.tFlow(
                context = this@MainActivity,
                id = R.string.description
            ).collect { value ->
                withContext(Dispatchers.Main) {
                    basic.text = value
                }
            }
        }
        lifecycleScope.launch {
            tolgee.tFlow(
                context = this@MainActivity,
                id = R.string.percentage_placeholder,
                87
            ).collect { value ->
                withContext(Dispatchers.Main) {
                    parameter.text = value
                }
            }
        }
        lifecycleScope.launch {
            tolgee.tPluralFlow(
                resources = this@MainActivity.resources,
                id = R.plurals.plr_test_placeholder_2,
                3, 2, 3, "Plurals"
            ).collect { value ->
                withContext(Dispatchers.Main) {
                    plural.text = value
                }
            }
        }
        lifecycleScope.launch {
            tolgee.tArrayFlow(
                resources = this@MainActivity.resources,
                id = R.array.array_test
            ).collect { value ->
                withContext(Dispatchers.Main) {
                    array.text = value.joinToString()
                }
            }
        }

        buttonEn.setOnClickListener {
            tolgee.setLocale(Locale.ENGLISH)
        }
        buttonFr.setOnClickListener {
            tolgee.setLocale(Locale.FRENCH)
        }
        buttonCs.setOnClickListener {
            tolgee.setLocale(Locale("cs"))
        }
    }
}
