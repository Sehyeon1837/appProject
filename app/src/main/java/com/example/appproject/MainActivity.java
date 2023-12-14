package com.example.appproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import parkMap.kakaoMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private RadioGroup choiceYear;
    private Button mapBtn;
    private TextView NowTemperature, NowUHI, uhiInfo;
    readfiles readfile;
    mapMarker mapmarker;
    String inputYear;
    private weatherData wedata;
    private double tempUhi;

    private ParkCsvReader parkCsvReader;
    private LocationManager locationManager;
    private Location location;
    private ArrayList<ArrayList> parkInfoArray;
    private ScrollView scrollView;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merged_main);

        Resources res = getResources();

        choiceYear = findViewById(R.id.choiceyear);
        NowTemperature = findViewById(R.id.NowTemperature);
        NowUHI = findViewById(R.id.NowUHI);
        uhiInfo = findViewById(R.id.uhiInfo);
        scrollView = findViewById(R.id.scrollView);
        setAPIinfo();

        // 퀴즈 버튼, 가이드 버튼
        Button quizbutton = findViewById(R.id.QuizButton);
        ImageButton rightbutton = findViewById(R.id.GuideButton);

        // 퀴즈 버튼 활성화
        quizbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QuizButton.class);
                startActivity(intent);
            }
        });

        // 가이드 버튼 활성화
        rightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GuideButton.class);
                startActivity(intent);
            }
        });

        getAppKeyHash();

        setKakaoMap();
        readfile = new readfiles(res, this);
        readfile.setYear("2017");

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapmarker = new mapMarker(res, mapFragment, readfile, MainActivity.this);
        mapmarker.setStart(new LatLng(location.getLatitude(), location.getLongitude()));

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

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            // 지도 위에서의 모든 터치는 스크롤뷰로 처리 못하게 함
            if (isTouchOnMap(ev)) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return super.dispatchTouchEvent(ev);
            }
        }

        scrollView.requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(ev);
    }

    private boolean isTouchOnMap(MotionEvent ev) {
        if (mapFragment == null || mapFragment.getView() == null) {
            return false;
        }

        int[] location = new int[2];
        mapFragment.getView().getLocationInWindow(location);

        Rect fragmentRect = new Rect(location[0], location[1],
                location[0] + mapFragment.getView().getWidth(),
                location[1] + mapFragment.getView().getHeight());

        // 영역 확인
        return fragmentRect.contains((int) ev.getRawX(), (int) ev.getRawY());
    }

    public void setAPIinfo() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if(minute >= 30)
            minute = 3;
        else
            minute = 0;

        hour--; // 기상청 api는 실시간으로 접근하면 데이터가 없는 경우도 있어서 1시간 전으로 접근
        String currentDate = year + "" + month + "" + day;
        String currentTime = hour + "" + minute + "0";
        if(hour < 10)
            currentTime = "0" + hour + "" + minute + "0";

        wedata = new weatherData("63", "89", currentDate, currentTime);
        Log.d("now", currentDate + " 시간: " + currentTime);

        tempUhi = wedata.getUHI();

        NowTemperature.setText(wedata.getTmp());
        NowUHI.setText("현재 열섬 지수: "+ String.format("%.2f",  tempUhi));

        if(tempUhi >= 90)
            uhiInfo.setText("오늘의 UHI는 높은 편입니다!");
        if(tempUhi >= 80 && tempUhi < 90)
            uhiInfo.setText("오늘의 UHI는 조금 높습니다.");
        else
            uhiInfo.setText("오늘의 UHI는 보통입니다.");

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