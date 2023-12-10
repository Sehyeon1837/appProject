package com.example.appproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class GuideButton extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_viewpager);

        ViewPager guideviewpager = findViewById(R.id.GuideViewPager);
        com.example.appproject.ViewPagerAdapter adapter = new com.example.appproject.ViewPagerAdapter(this);
        guideviewpager.setAdapter(adapter);

    }
}
