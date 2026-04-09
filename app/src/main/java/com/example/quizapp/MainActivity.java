package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /*
    On the main screen, the user enters their name and selects a theme.
     */
    EditText etName;
    Button btnStart;
    Switch switchTheme;
    SharedPreferences preferences;


    /*
    The name and theme are saved using SharedPreferences so they persist
    across the app. When the user clicks start, the quiz activity is launched.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences("QuizPrefs", MODE_PRIVATE);

        boolean isDarkMode = preferences.getBoolean("darkMode", false);
        if (isDarkMode) {
            setTheme(R.style.Theme_QuizApp_Dark);
        } else {
            setTheme(R.style.Theme_QuizApp_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        btnStart = findViewById(R.id.btnStart);
        switchTheme = findViewById(R.id.switchTheme);

        // restore saved name
        String savedName = preferences.getString("userName", "");
        etName.setText(savedName);

        // set switch state safely
        switchTheme.setOnCheckedChangeListener(null);
        switchTheme.setChecked(isDarkMode);

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean currentDarkMode = preferences.getBoolean("darkMode", false);

            if (isChecked == currentDarkMode) {
                return;
            }

            preferences.edit().putBoolean("darkMode", isChecked).apply();

            // restart activity to apply theme
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        btnStart.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();

            preferences.edit().putString("userName", name).apply();

            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("userName", name);
            startActivity(intent);
        });
    }
}