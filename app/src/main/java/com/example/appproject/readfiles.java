package com.example.appproject;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class readfiles extends MainActivity {
    String year, tempStr, areaname;
    double TotalAvgTem, AreaAvgTem, TotalAvgHum, AreaAvgHum;
    float[] meanData = new float[2];
    double[] UHITemp = new double[19];
    String[] datas = new String[19], area, columns;
    Resources res;
    Activity activity;
    DBHelper helper;

    public readfiles(Resources res, Activity activity) {
        this.activity = activity;
        this.res = res;
        helper = new DBHelper(activity);

        area = res.getStringArray(R.array.area_list);
        columns = res.getStringArray(R.array.columns_name);
    }

    public void setYear(String newYear) { //db 저장 및 확인 코드 추가
        year = newYear;

        List<DataModel> dataList = helper.getDataByYear(newYear);

        if (dataList.isEmpty()) {
            Log.d("Data", "해당 값이 없습니다.");
            readExcel();
        } else {
            Log.d("Data", "해당 값이 있습니다.");

            for (DataModel dataModel : dataList) {
                int id = dataModel.getId();
                String[] strarr = dataModel.getMyArrayColumn();
                double[] douarr = dataModel.getMyDoubleArrayColumn();
                String year = dataModel.getYear();
                //db에 있던 정보 저장
                datas = strarr;
                UHITemp = douarr;

                Log.d("Data", "ID: " + id + ", MyArray: " + Arrays.toString(strarr) +
                        ", MyDoubleArray: " + Arrays.toString(douarr) + ", Year: " + year);
            }
        }
    }

    public void setDataFromDB(String inputData, String inputUHI) { //split
        String[] strData = inputData.split("#");
        String[] strUHI = inputUHI.split("#");

        for(int i = 0; i < datas.length; i++)
            datas[i] = strData[i];

        for(int i = 0; i < UHITemp.length; i++) {
            double tempuhi = Double.parseDouble(strUHI[i]);
            UHITemp[i] = tempuhi;
        }
    }

    public String getDatas(int index) {
        return datas[index];
    }

    public double getUHITemp(int index) {
        return UHITemp[index];
    }

    public void readExcel() {
        boolean isTrue;
        try{
            TotalAvgTem = 0; AreaAvgTem = 0; TotalAvgHum = 0; AreaAvgHum = 0;
            for(int i = 0; i < datas.length; i++) {
                isTrue = false;
                String fileNameTemp = year + "/" + area[i] + year + ".xls";
                if(i == 13 && (year == "2020" || year == "2021")) {
                    fileNameTemp = year + "/" + "BackUp_14.효자5동 주민센터" + year + ".xls";
                    isTrue = true;
                    i++;
                }

                InputStream is = activity.getResources().getAssets().open(fileNameTemp);
                Workbook wb = Workbook.getWorkbook(is);
                tempStr = readFile(wb);

                areaname = area[i].substring(area[i].lastIndexOf(".")+1);

                datas[i] =  tempStr;
                Log.d("data: ", areaname + ": " + datas[i] + '\n');

                UHITemp[i] = calcHI(AreaAvgTem, AreaAvgHum);
                Log.d("section: ", (i+1) + "번째 구역 UHI: " + String.format("%.2f",UHITemp[i]) + '\n');


                if(isTrue) {
                    datas[i - 1] = "";
                    UHITemp[i - 1] = 0.0;
                }

                if(!isTrue && i == 13) {
                    i++;
                    datas[i] = "";
                    UHITemp[i] = 0.0;
                }

            }
            helper.insertArrays(datas, UHITemp, year);

        } catch(IOException | BiffException e) {
            e.printStackTrace();
        }
    }

    private String readFile(Workbook wb) {
        float[] sumData = {0, 0};
        tempStr = "";
        if(wb != null) {
            Sheet sheet = wb.getSheet(0);
            if(sheet != null) {
                int rowIndexStart = 8;
                if(year == "2020" || year == "2021") //2020,2021 = 1부터 시작
                    rowIndexStart = 1;
                int rowTotal = sheet.getColumn(1).length;

                StringBuilder sb;
                for(int row = rowIndexStart; row < rowTotal; row++) { //2122, rowTotal
                    for(int col = 3; col <= 4; col++) { //시간 생략, 5개 값만 받아옴
                        sb = new StringBuilder();
                        try {
                            String contents = sheet.getCell(col, row).getContents(); //contents에 해당 값 들어가 있음
                            double temp = Double.parseDouble(contents);
                            sumData[col-3] += temp;
                        } catch (Exception e) {
                            if(col == 1)
                                rowTotal = row;
                            break;
                        }
                    }
                }

                for(int i = 0; i < sumData.length; i++)
                    meanData[i] = sumData[i] / (rowTotal - rowIndexStart);

                AreaAvgTem = meanData[0];
                AreaAvgHum = meanData[1];

                tempStr = tempStr + ("평균 " + columns[0] + ": " + String.format("%.2f", meanData[0]) + " ");
            }
        }
        return tempStr;
    }

    private double calcHI(double tem, double hum) { //열섬 계산식
        double C = tem;
        double T = (C * 9/5) + 32;
        double RH = hum;

        double hi = -42.379 + 2.04901523*T + 10.14333127*RH - .22475541*T*RH - .00683783*T*T - .05481717*RH*RH + .00122874*T*T*RH + .00085282*T*RH* RH - .00000199*T*T*RH*RH;

        return hi;
    }
}
