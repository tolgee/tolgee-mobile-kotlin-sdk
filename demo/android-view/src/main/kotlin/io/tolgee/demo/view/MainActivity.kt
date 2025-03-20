package io.tolgee.demo.view

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dev.datlag.tolgee.Tolgee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val basic = findViewById<TextView>(R.id.basic_text)
        val parameter = findViewById<TextView>(R.id.parameterized_text)
        val button = findViewById<Button>(R.id.button)

        val tolgee = Tolgee.instanceOrInit {
            contentDelivery {
                id(System.getenv("TOLGEE_API_KEY"))
                formatter(Tolgee.Formatter.Sprintf)
            }
        }

        lifecycleScope.launch {
            tolgee.translation(
                context = this@MainActivity,
                id = R.string.description
            ).collect { value ->
                withContext(Dispatchers.Main) {
                    basic.text = value
                }
            }
        }
        lifecycleScope.launch {
            tolgee.translation(
                context = this@MainActivity,
                id = R.string.percentage_placeholder,
                87
            ).collect { value ->
                withContext(Dispatchers.Main) {
                    parameter.text = value
                }
            }
        }
        button.setOnClickListener {
            tolgee.setLocale(Locale.ENGLISH)
        }
    }
}