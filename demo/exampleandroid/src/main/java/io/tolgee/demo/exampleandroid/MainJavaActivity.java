package io.tolgee.demo.exampleandroid;

import android.content.Context;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import io.tolgee.TolgeeAndroid;
import io.tolgee.Tolgee;
import io.tolgee.TolgeeContextWrapper;

import java.util.Locale;

public class MainJavaActivity extends ComponentActivity implements Tolgee.ChangeListener {

    private final TolgeeAndroid tolgee = Tolgee.getInstance();

    @Override
    protected void attachBaseContext(Context newBase) {
        // Wrapping base context will make sure getString calls will use tolgee
        // even for instances which cannot be replaced automatically by the compiler
        super.attachBaseContext(TolgeeContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tolgee.addChangeListener(this);

        setContentView(R.layout.activity_main);

        // Make sure the app title stays updated
        setTitle(R.string.app_name);

        TextView name = findViewById(R.id.app_name_text);
        TextView basic = findViewById(R.id.basic_text);
        TextView parameter = findViewById(R.id.parameterized_text);
        TextView plural = findViewById(R.id.plural_text);
        TextView array = findViewById(R.id.array_text);
        Button buttonEn = findViewById(R.id.button_en);
        Button buttonFr = findViewById(R.id.button_fr);
        Button buttonCs = findViewById(R.id.button_cs);

        // Update texts within the app with translated ones
        name.setText(getString(R.string.app_name));
        basic.setText(getString(R.string.description));
        parameter.setText(getString(R.string.percentage_placeholder, "87"));
        plural.setText(getResources().getQuantityString(R.plurals.plr_test_placeholder_2, 2, 3, "Plurals"));
        array.setText(String.join(", ", getResources().getStringArray(R.array.array_test)));

        buttonEn.setOnClickListener(v -> {
            tolgee.setLocale(Locale.ENGLISH);
            tolgee.preload(this);
        });
        buttonFr.setOnClickListener(v -> {
            tolgee.setLocale(Locale.FRENCH);
            tolgee.preload(this);
        });
        buttonCs.setOnClickListener(v -> {
            tolgee.setLocale("cs");
            tolgee.preload(this);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Make sure the translations are loaded
        // This function will initiate translations fetching in the background and
        // will trigger changeFlow whenever updated translations are available
        tolgee.preload(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        tolgee.removeChangeListener(this);
    }

    @Override
    public void onTranslationsChanged() {
        // we want to reload activity after a language change
        recreate();
    }
}