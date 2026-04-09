package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView tvResultMessage, tvScore;
    Button btnNewQuiz, btnFinish;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        SharedPreferences is used to store small pieces of data like the user’s
        name and theme preference so they persist across activities.
         */
        preferences = getSharedPreferences("QuizPrefs", MODE_PRIVATE);

        boolean isDarkMode = preferences.getBoolean("darkMode", false);
        if (isDarkMode) {
            setTheme(R.style.Theme_QuizApp_Dark);
        } else {
            setTheme(R.style.Theme_QuizApp_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvResultMessage = findViewById(R.id.tvResultMessage);
        tvScore = findViewById(R.id.tvScore);
        btnNewQuiz = findViewById(R.id.btnNewQuiz);
        btnFinish = findViewById(R.id.btnFinish);

        String userName = getIntent().getStringExtra("userName");
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);

        tvResultMessage.setText("Congratulations " + userName + "!");
        tvScore.setText("Your score: " + score + "/" + total);

        btnNewQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnFinish.setOnClickListener(v -> finishAffinity());
    }
}

/*
The result screen displays the final score along with the user’s name. The
user can either restart the quiz or exit the application.
 */