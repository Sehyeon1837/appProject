package com.example.appproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import parkMap.kakaoMap;
import com.google.android.gms.maps.SupportMapFragment;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RadioGroup choiceYear;
    private Button mapBtn;
    readfiles readfile;
    mapMarker mapmarker;
    String inputYear;

    private ParkCsvReader parkCsvReader;
    private LocationManager locationManager;
    private Location location;
    private ArrayList<ArrayList> parkInfoArray;

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
        mapmarker = new mapMarker(res, mapFragment, readfile, MainActivity.this);

        setKakaoMap();

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
                mapmarker.changeYear(inputYear);
            }
        });
    }

    public void setKakaoMap(){
        // 권한ID를 가져옵니다
        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET);

        int permission2 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permission3 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1000);
            }
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // GPS로 위치 정보를 못받을 경우 네트워크로 위치 정보를 받아옴
        location = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ?
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) : locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null){
            location = new Location("null");
            location.setLatitude(35.8242238);
            location.setLongitude(127.1479532);
        }

        parkCsvReader = new ParkCsvReader(location);
        parkInfoArray = parkCsvReader.readCsv(this);

        mapBtn = findViewById(R.id.openMapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), kakaoMap.class);
                ArrayList<String> array = new ArrayList<String>();
                array.add(Double.toString(location.getLatitude()));
                array.add(Double.toString(location.getLongitude()));
                parkInfoArray.add(array);
                intent.putExtra("parkInfo", parkInfoArray);
                startActivity(intent); // 지도 뷰 띄우기
            }
        });
    }

    // 권한 체크 이후로직
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == false) {
                finish();
            }
            else setKakaoMap();
        }
    }
}