package com.example.appproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class QuizButton extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstancesState) {

        super.onCreate(savedInstancesState);
        setContentView(R.layout.quiz_page);

        // 우측 상단에 나가기 버튼 생성
        ImageButton exitbuttonforquiz = findViewById(R.id.ExitButtonforQuiz);

        exitbuttonforquiz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
