package com.example.appproject;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParkCsvReader {
    static double userLatitude;
    static double userLongitude;
    public ParkCsvReader(double latitude, double longitude){
        userLatitude = latitude;
        userLongitude = longitude;
    }
    public static List<String> readCsv(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.jeonju_park_info);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        List<String> parkInfo = new ArrayList<String>();
        List<String> parkNum = new ArrayList<String>(); // 공원 관리번호
        List<Double> Latitude = new ArrayList<Double>(); // 위도
        List<Double> Longitude = new ArrayList<Double>(); // 경도
        List<Double> Distance = new ArrayList<Double>(); // 사용자의 위치와 공원의 직선 거리
        String info = new String();


        try {
            String line = "";
            info = br.readLine();
            while ((line = br.readLine()) != null) {
                parkInfo.add(line);
                String array[] = line.split(",");
                parkNum.add(array[0]); // parkNum에 공원 관리번호 저장
                Latitude.add(Double.parseDouble(array[5])); // Latitude에 공원 위도 저장
                Longitude.add(Double.parseDouble(array[6])); // Longitude에 공원 경도 저장
                Distance.add(Math.sqrt(Math.pow((userLatitude - Latitude.get(Latitude.size()-1)), 2) + Math.pow((userLongitude - Longitude.get(Longitude.size()-1)), 2)));
            }


            //거리 순 정렬
            double[] distanceArray = Distance.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
            sortDistanceByQuickSort(parkInfo, distanceArray);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return parkInfo; // 거리 순으로 정렬된 리스트 반환
    }


    // 거리순 정렬
    public static void sortDistanceByQuickSort(List parkInfo, double[] arr) {
        quickSort(parkInfo, arr, 0, arr.length - 1);
    }
    public static void quickSort(List parkInfo, double[] arr, int left, int right) {
        int part = partition(parkInfo, arr, left, right);
        if (left < part - 1) {
            quickSort(parkInfo, arr, left, part - 1);
        }
        if (part < right) {
            quickSort(parkInfo, arr, part, right);
        }
    }
    public static int partition(List parkInfo, double[] arr, int left, int right) {
        double pivot = arr[(left + right) / 2];
        while (left <= right) {
            while (arr[left] < pivot) {
                left++;
            }
            while (arr[right] > pivot) {
                right--;
            }
            if (left <= right) {
                swap(parkInfo, arr, left, right);
                left++;
                right--;
            }
        }
        return left;
    }

    private static void swap(List parkInfo, double[] arr, int left, int right) {
        double tempDouble;
        String tempString;
        tempDouble = arr[left];
        arr[left] = arr[right];
        arr[right] = tempDouble;

        tempString = parkInfo.get(left).toString();
        parkInfo.set(left, parkInfo.get(right));
        parkInfo.set(right, tempString);
    }
}