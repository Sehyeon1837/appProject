package com.example.appproject;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class readfiles extends MainActivity {
    String year, tempStr;
    double AvgTemp, tempAvgTem;
    float[] meanData = new float[5];
    double[] UHITemp = new double[18];
    String[] datas = new String[18], area, columns;
    TextView text;
    Resources res;
    Activity activity;

    public readfiles(TextView text, Resources res, Activity activity) {
        this.text = text;
        this.activity = activity;

        this.res = res;
        area = res.getStringArray(R.array.area_list);
        columns = res.getStringArray(R.array.columns_name);
    }

    public void setYear(String newYear) {
        year = newYear;
        readExcel();
    }

    public void readExcel() {
        try{
            text.setText("");
            AvgTemp = 0; tempAvgTem = 0;
            for(int i = 0; i < datas.length; i++) {
                String fileNameTemp = year + "/" + area[i] + year + ".xls";
                if(i == 13 && (year == "2020" || year == "2021"))
                    fileNameTemp = year + "/" + "BackUp_14.효자5동 주민센터" + year + ".xls";

                InputStream is = activity.getResources().getAssets().open(fileNameTemp);
                Workbook wb = Workbook.getWorkbook(is);
                tempStr = readFile(wb);
                AvgTemp += tempAvgTem;
                datas[i] = area[i].substring(area[i].lastIndexOf(".")+1) + ": " + tempStr;
                //text.append(datas[i]);
                tempAvgTem = Math.round(tempAvgTem);
                text.append((i+1) + "번쩨 구역 평균 기온: " + tempAvgTem + '\n');
            }
            AvgTemp /= datas.length;
            AvgTemp = Math.round(AvgTemp);
            text.append(year + "년도 전체 구역 평균 기온: " + AvgTemp + " ");

        } catch(IOException | BiffException e) {
            e.printStackTrace();
        }
    }

    private String readFile(Workbook wb) {
        float[] sumData = {0, 0, 0, 0, 0};
        tempAvgTem = 0;
        tempStr = "";
        if(wb != null) {
            Sheet sheet = wb.getSheet(0);
            if(sheet != null) {
                int rowIndexStart = 8;
                if(year == "2020" || year == "2021") //2020,2021 = 1부터 시작
                    rowIndexStart = 1;
                int rowTotal = sheet.getColumn(1).length;
                int rowTemp = rowTotal;

                StringBuilder sb;
                for(int row = rowIndexStart; row < rowTotal; row++) { //2122, rowTotal
                    sb = new StringBuilder();
                    for(int col = 1; col < 6; col++) { //시간 생략, 5개 값만 받아옴
                        try {
                            String contents = sheet.getCell(col, row).getContents(); //contents에 해당 값 들어가 있음
                            double temp = Double.parseDouble(contents);
                            if(col == 3)
                                tempAvgTem += temp;
                            sumData[col-1] += temp;
                        } catch (Exception e) {
                            if(col == 1)
                                rowTotal = row;
                            break;
                        }
                    }
                }
                tempAvgTem /= (rowTotal - rowIndexStart);

                for(int i = 0; i < sumData.length; i++) {
                    meanData[i] = sumData[i]/(rowTotal - rowIndexStart); //2114 rowTotal - rowIndexStart + 1
                    tempStr = tempStr + (columns[i] + ": " + meanData[i]);
                }
            }
        }
        return tempStr;
    }
}
