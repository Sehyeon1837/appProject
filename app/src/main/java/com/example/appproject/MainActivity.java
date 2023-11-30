package com.example.appproject;

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
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import parkMap.kakaoMap;

public class MainActivity extends AppCompatActivity {
    private RadioGroup choiceYear;
    private Button mapBtn;
    String year = "2017";
    String tempStr;
    TextView text;
    float[] meanData = new float[5];
    String[] datas = new String[18];
    String[] area, columns;

    private ParkCsvReader parkCsvReader;
    private LocationManager locationManager;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        area = res.getStringArray(R.array.area_list);
        columns = res.getStringArray(R.array.columns_name);

        choiceYear = findViewById(R.id.choiceyear);
        text = findViewById(R.id.text);
        readExcel();

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
        parkCsvReader = new ParkCsvReader(location);
        ArrayList<ArrayList> parkInfoArray = parkCsvReader.readCsv(this);

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

        choiceYear.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.btu2017) {
                    year = "2017";
                } else if(checkedId == R.id.btu2018) {
                    year = "2018";
                } else if(checkedId == R.id.btu2019) {
                    year = "2019";
                } else if(checkedId == R.id.btu2020) {
                    year = "2020";
                } else if(checkedId == R.id.btu2021) {
                    year = "2021";
                }
                readExcel();
            }
        });
    }

    public void readExcel() {
        try{
            text.setText("");
            for(int i = 0; i < datas.length; i++) {
                String fileNameTemp = year + "/" + area[i] + year + ".xls";
                if(i == 13 && (year == "2020" || year == "2021"))
                    fileNameTemp = year + "/" + "BackUp_14.효자5동 주민센터" + year + ".xls";

                InputStream is = getBaseContext().getResources().getAssets().open(fileNameTemp);
                Workbook wb = Workbook.getWorkbook(is);
                readFile(wb);
                datas[i] = readFile(wb);
                text.append(area[i].substring(area[i].lastIndexOf(".")+1) + ": " + datas[i]);
            }

        } catch(IOException | BiffException e) {
            e.printStackTrace();
        }
    }

    private String readFile(Workbook wb) {
        float[] sumData = {0, 0, 0, 0, 0};
        tempStr = "";
        if(wb != null) {
            Sheet sheet = wb.getSheet(0);
            if(sheet != null) {
                int rowIndexStart = 8;
                if(year == "2020" || year == "2021") //2020,2021 = 1부터 시작
                    rowIndexStart = 1;
                int rowTotal = sheet.getColumn(0).length;
                int rowTemp = rowTotal;

                StringBuilder sb;
                for(int row = rowIndexStart; row < rowTotal; row++) { //2122, rowTotal
                    sb = new StringBuilder();
                    for(int col = 1; col < 6; col++) { //시간 생략, 5개 값만 받아옴
                        try {
                            String contents = sheet.getCell(col, row).getContents(); //contents에 해당 값 들어가 있음
                            double temp = Double.parseDouble(contents);
                            sumData[col-1] += temp;
                        } catch (Exception e) {
                            rowTemp = row;
                            row = rowTotal;
                            break;
                        }
                    }
                }

                for(int i = 0; i < sumData.length; i++) {
                    meanData[i] = sumData[i]/(rowTemp - rowIndexStart); //2114 rowTotal - rowIndexStart + 1
                    tempStr = tempStr + (columns[i] + ": " + meanData[i]);
                }
            }
        }
        return tempStr;
    }
}