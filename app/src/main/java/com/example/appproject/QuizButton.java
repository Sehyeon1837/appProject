package com.example.appproject;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class QuizButton extends AppCompatActivity {

    private TextView questionTextView;
    private EditText answerEditText;
    private TextView hintTextView;
    private ArrayList<Quiz> quizzes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_page);

        // 우측 상단에 나가기 버튼 생성
        ImageButton exitbuttonforquiz = findViewById(R.id.ExitButtonforQuiz);

        exitbuttonforquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 퀴즈 로직 활성화
        questionTextView = findViewById(R.id.WriteQuiz);
        answerEditText = findViewById(R.id.WriteAnswer);
        hintTextView = findViewById(R.id.ShowHint);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.quizs)));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 3) {
                    Quiz quiz = new Quiz(parts[0], parts[1], parts[2]); // part 0 문제 part 1 답 / part 2 힌트
                    quizzes.add(quiz);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        int randomIndex = random.nextInt(quizzes.size());
        Quiz randomQuiz = quizzes.get(randomIndex);

        Button btnCheck = findViewById(R.id.SendMyAnswer);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = answerEditText.getText().toString();
                if (userAnswer.equals(randomQuiz.getAnswer())) {
                    Toast.makeText(QuizButton.this, "정답입니다!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(QuizButton.this, "오답입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        questionTextView.setText(randomQuiz.getQuestion());
        hintTextView.setText(randomQuiz.getHint());

        findViewById(R.id.ShowHint).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    hintTextView.setText(randomQuiz.getHint());
                } else if (action == MotionEvent.ACTION_UP) {
                    hintTextView.setText("힌트 보기");
                }
                return true;
            }
        });
    }

    public class Quiz {
        private String question;
        private String answer;
        private String hint;

        public Quiz(String question, String answer, String hint) {
            this.question = question;
            this.answer = answer;
            this.hint = hint;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }

        public String getHint() {
            return hint;
        }
    }
}
