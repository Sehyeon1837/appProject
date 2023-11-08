package com.example.appproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {
    private RadioGroup choiceYear;
    readfiles readfile;
    String inputYear;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();

        choiceYear = findViewById(R.id.choiceyear);
        text = findViewById(R.id.text);

        readfile = new readfiles(text, res, this);
        readfile.setYear("2017");

        choiceYear.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.btu2017) {
                    inputYear = "2017";
                } else if(checkedId == R.id.btu2018) {
                    inputYear = "2018";
                } else if(checkedId == R.id.btu2019) {
                    inputYear = "2019";
                } else if(checkedId == R.id.btu2020) {
                    inputYear = "2020";
                } else if(checkedId == R.id.btu2021) {
                    inputYear = "2021";
                }
                readfile.setYear(inputYear);
            }
        });
    }
}