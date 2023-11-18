package com.example.appproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity {
    private RadioGroup choiceYear;
    readfiles readfile;
    mapMarker mapmarker;
    String inputYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();

        choiceYear = findViewById(R.id.choiceyear);

        readfile = new readfiles(res, this);
        readfile.setYear("2017");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapmarker = new mapMarker(res, mapFragment, readfile);

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
                mapmarker.changeYear();
            }
        });
    }
}