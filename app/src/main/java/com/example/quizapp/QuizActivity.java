package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    TextView tvQuestion, tvProgressText;
    Button btnOption1, btnOption2, btnOption3, btnOption4, btnSubmit, btnNext;
    ProgressBar progressBar;
    Switch switchThemeQuiz;

    ArrayList<Question> questionList;
    int currentQuestionIndex = 0;
    int selectedAnswerIndex = -1;
    int score = 0;
    boolean answered = false;
    String userName;

    SharedPreferences preferences;

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
        setContentView(R.layout.activity_quiz);

        userName = getIntent().getStringExtra("userName");

        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgressText = findViewById(R.id.tvProgressText);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);
        switchThemeQuiz = findViewById(R.id.switchThemeQuiz);

        /*
        The app stores all quiz questions in an ArrayList using a custom
        Question class. Each question contains the text, options, and the
        correct answer index.
         */
        questionList = new ArrayList<>();

        loadQuestions();

        progressBar.setMax(questionList.size());

        currentQuestionIndex = getIntent().getIntExtra("currentQuestionIndex", 0);
        score = getIntent().getIntExtra("score", 0);

        boolean restoredAnswered = getIntent().getBooleanExtra("answered", false);
        int restoredSelectedAnswerIndex = getIntent().getIntExtra("selectedAnswerIndex", -1);

        /*
        Each time a question is displayed, the app updates the UI with the
        question and options, resets button colors, and updates the progress
        bar.
         */
        displayQuestion();

        /*
        When the user selects an option, the app stores the selected answer
        index and highlights the selected option.
         */
        if (restoredAnswered && restoredSelectedAnswerIndex != -1) {
            selectedAnswerIndex = restoredSelectedAnswerIndex;
            answered = true;
            restoreAnsweredState();
        }

        switchThemeQuiz.setOnCheckedChangeListener(null);
        switchThemeQuiz.setChecked(isDarkMode);

        switchThemeQuiz.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean currentDarkMode = preferences.getBoolean("darkMode", false);

            if (isChecked == currentDarkMode) {
                return;
            }

            preferences.edit().putBoolean("darkMode", isChecked).apply();

            Intent intent = new Intent(QuizActivity.this, QuizActivity.class);
            intent.putExtra("userName", userName);
            intent.putExtra("currentQuestionIndex", currentQuestionIndex);
            intent.putExtra("score", score);
            intent.putExtra("answered", answered);
            intent.putExtra("selectedAnswerIndex", selectedAnswerIndex);

            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        btnOption1.setOnClickListener(v -> selectAnswer(0));
        btnOption2.setOnClickListener(v -> selectAnswer(1));
        btnOption3.setOnClickListener(v -> selectAnswer(2));
        btnOption4.setOnClickListener(v -> selectAnswer(3));

        /*
        When the user submits, the app compares the selected answer with the
        correct answer. It gives visual feedback by coloring the correct
        option green and incorrect option red, and prevents further changes.
         */
        btnSubmit.setOnClickListener(v -> submitAnswer());
        /*
        After submission, the user can move to the next question. When all
        questions are completed, the app navigates to the result screen.
         */
        btnNext.setOnClickListener(v -> goToNextQuestion());
    }

    private void loadQuestions() {
        questionList.add(new Question(
                "What is the capital of Australia?",
                new String[]{"Sydney", "Canberra", "Melbourne", "Perth"},
                1
        ));

        questionList.add(new Question(
                "Which language is used for Android development in this task?",
                new String[]{"Python", "Java", "C++", "Swift"},
                1
        ));

        questionList.add(new Question(
                "Which component is used to show quiz progress?",
                new String[]{"ListView", "ProgressBar", "Toast", "Intent"},
                1
        ));

        questionList.add(new Question(
                "Where is small persistent data stored in this app?",
                new String[]{"Firebase", "SQLite", "SharedPreferences", "Room"},
                2
        ));

        questionList.add(new Question(
                "Which method is used to close all app activities?",
                new String[]{"finish()", "finishAffinity()", "closeApp()", "stopActivity()"},
                1
        ));
    }

    private void displayQuestion() {
        answered = false;
        selectedAnswerIndex = -1;

        Question currentQuestion = questionList.get(currentQuestionIndex);

        tvQuestion.setText(currentQuestion.getQuestionText());
        btnOption1.setText(currentQuestion.getOptions()[0]);
        btnOption2.setText(currentQuestion.getOptions()[1]);
        btnOption3.setText(currentQuestion.getOptions()[2]);
        btnOption4.setText(currentQuestion.getOptions()[3]);

        resetButtonColors();
        enableOptionButtons(true);

        btnSubmit.setEnabled(true);
        btnNext.setEnabled(false);

        /*
        he progress bar dynamically updates to show how many questions the
        user has completed.
         */
        progressBar.setProgress(currentQuestionIndex + 1);
        tvProgressText.setText("Question " + (currentQuestionIndex + 1) + " of " + questionList.size());
    }

    private void restoreAnsweredState() {
        int correctIndex = questionList.get(currentQuestionIndex).getCorrectAnswerIndex();

        if (selectedAnswerIndex == correctIndex) {
            Button correctButton = getButtonByIndex(correctIndex);
            correctButton.setBackgroundColor(Color.parseColor("#2E7D32"));
            correctButton.setTextColor(Color.WHITE);
        } else {
            Button wrongButton = getButtonByIndex(selectedAnswerIndex);
            wrongButton.setBackgroundColor(Color.parseColor("#C62828"));
            wrongButton.setTextColor(Color.WHITE);

            Button correctButton = getButtonByIndex(correctIndex);
            correctButton.setBackgroundColor(Color.parseColor("#2E7D32"));
            correctButton.setTextColor(Color.WHITE);
        }

        enableOptionButtons(false);
        btnSubmit.setEnabled(false);
        btnNext.setEnabled(true);
    }

    private void selectAnswer(int index) {
        if (answered) return;

        selectedAnswerIndex = index;
        resetButtonColors();

        Button selectedButton = getButtonByIndex(index);
        selectedButton.setBackgroundColor(Color.parseColor("#455A64"));
        selectedButton.setTextColor(Color.WHITE);
    }

    private void submitAnswer() {
        if (selectedAnswerIndex == -1 || answered) {
            return;
        }

        answered = true;
        int correctIndex = questionList.get(currentQuestionIndex).getCorrectAnswerIndex();

        if (selectedAnswerIndex == correctIndex) {
            score++;
            Button correctButton = getButtonByIndex(correctIndex);
            correctButton.setBackgroundColor(Color.parseColor("#2E7D32"));
            correctButton.setTextColor(Color.WHITE);
        } else {
            Button wrongButton = getButtonByIndex(selectedAnswerIndex);
            wrongButton.setBackgroundColor(Color.parseColor("#C62828"));
            wrongButton.setTextColor(Color.WHITE);

            Button correctButton = getButtonByIndex(correctIndex);
            correctButton.setBackgroundColor(Color.parseColor("#2E7D32"));
            correctButton.setTextColor(Color.WHITE);
        }

        enableOptionButtons(false);
        btnSubmit.setEnabled(false);
        btnNext.setEnabled(true);
    }

    private void goToNextQuestion() {
        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            displayQuestion();
        } else {
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("userName", userName);
            intent.putExtra("score", score);
            intent.putExtra("total", questionList.size());
            startActivity(intent);
            finish();
        }
    }

    private Button getButtonByIndex(int index) {
        switch (index) {
            case 0:
                return btnOption1;
            case 1:
                return btnOption2;
            case 2:
                return btnOption3;
            default:
                return btnOption4;
        }
    }

    private void resetButtonColors() {
        btnOption1.setBackgroundColor(Color.parseColor("#424242"));
        btnOption1.setTextColor(Color.WHITE);

        btnOption2.setBackgroundColor(Color.parseColor("#424242"));
        btnOption2.setTextColor(Color.WHITE);

        btnOption3.setBackgroundColor(Color.parseColor("#424242"));
        btnOption3.setTextColor(Color.WHITE);

        btnOption4.setBackgroundColor(Color.parseColor("#424242"));
        btnOption4.setTextColor(Color.WHITE);
    }

    private void enableOptionButtons(boolean enable) {
        btnOption1.setEnabled(enable);
        btnOption2.setEnabled(enable);
        btnOption3.setEnabled(enable);
        btnOption4.setEnabled(enable);
    }
}