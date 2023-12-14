package com.example.appproject;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.zip.DeflaterOutputStream;

public class weatherData {
    private String nx = "63";	//위도
    private String ny = "89";	//경도
    private String baseDate = "20231212";	//조회하고싶은 날짜
    private String baseTime = "0500";	//조회하고싶은 시간
    private String type = "json";	//조회하고 싶은 type(json, xml 중 고름)
    private String tmperature = "", humidity = "";
    private CountDownLatch latch = new CountDownLatch(1);

    public weatherData(String nx, String ny, String baseDate, String baseTime) {
        this.nx = nx;
        this.ny = ny;
        this.baseDate = baseDate;
        this.baseTime = baseTime;

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    lookUpWeather();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });
    }

    public String getTmp() {
        try {
            latch.await();  // lookUpWeather()가 완료될 때까지 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return tmperature + "°C";
    }

    public double getUHI() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double reTmp =Double.parseDouble(tmperature);
        double reHum = Double.parseDouble(humidity);

        double C = reTmp;
        double T = (C * 9/5) + 32;
        double RH = reHum;

        double hi =  0.5 * (T + 61.0 + ((T-68.0)*1.2) + (RH*0.094));

        Log.d("uhi", "getUHI(): "+ hi);
        return hi;
    }

    public void lookUpWeather() throws IOException, JSONException {
        String urls = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?serviceKey=zXqVc7XeawOZMPwsriC2GndLqWpY93npE%2B%2F%2F2LG9vhAXUJAhLoT5P%2F2PqXze448krVzDiZVThzor%2Fzw3ojU0oQ%3D%3D&pageNo=1&numOfRows=8&dataType=JSON&" +
                "base_date=" + baseDate +
                "&base_time=" + baseTime +
                "&nx=" + nx +
                "&ny=" + ny;

        URL url = new URL(urls);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();
        String result= sb.toString();

        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");

        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");

        JSONObject jsonObj_4 = new JSONObject(items);
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");

        for(int i=0;i<jsonArray.length();i++){
            jsonObj_4 = jsonArray.getJSONObject(i);
            String obsrValue = jsonObj_4.getString("obsrValue");
            String category = jsonObj_4.getString("category");

            if(category.equals("T3H") || category.equals("T1H")){
                tmperature = obsrValue;
            }

            if(category.equals("REH"))
                humidity = obsrValue;
        }
    }

}
