package com.example.appproject;

import android.content.Context;
import android.location.Location;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class ParkCsvReader {
    static Location location = new Location("User");
    public ParkCsvReader(Location userLocation){
        location = userLocation;
    }
    public static ArrayList<ArrayList> readCsv(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.jeonju_park_info);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        ArrayList<ArrayList> parkInfoArray = new ArrayList<ArrayList>();
        ArrayList<String> parkInfo = new ArrayList<String>();
        ArrayList<String> parkInfoDistance = new ArrayList<String>();
        ArrayList<String> parkInfoArea = new ArrayList<String>();
        ArrayList<String> parkInfoScore = new ArrayList<String>();
        ArrayList<Double> Area = new ArrayList<Double>();
        ArrayList<Double> Score = new ArrayList<Double>();
        ArrayList<Integer> facilityNum = new ArrayList<Integer>();
        ArrayList<Double> Distance = new ArrayList<Double>(); // 사용자의 위치와 공원의 직선 거리
        String info = new String();
        Location parkLocation = new Location("park");

        try {
            String line = "";
            info = br.readLine();
            while ((line = br.readLine()) != null) {
                String array[] = line.split(",");
                parkLocation.setLatitude(Double.parseDouble(array[5]));
                parkLocation.setLongitude(Double.parseDouble(array[6]));
                Distance.add((double)location.distanceTo(parkLocation));
                line += "," + Distance.get(Distance.size()-1).intValue();
                parkInfo.add(line);
            }

            //거리 순 정렬
            double[] distanceArray = Distance.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
            sortParkInfoByQuickSort(parkInfo, distanceArray);

            int maxFacilityNum = 0;
            Double maxArea = 0.0;
            for(int i=0; i<10; i++){
                String array[] = parkInfo.get(i).split(",");
                parkInfoDistance.add(parkInfo.get(i));
                parkInfoArea.add(parkInfo.get(i));
                parkInfoScore.add(parkInfo.get(i));
                Area.add(Double.parseDouble(array[7]));
                int tempFacilityNum = 0;
                for(int j=8; j<13; j++){
                    if(array[j] != "") {
                        tempFacilityNum += array[j].length() - array[j].replace("+", "").length() + 1;
                        if(tempFacilityNum > maxFacilityNum) maxFacilityNum = tempFacilityNum;
                    }
                }
                facilityNum.add(tempFacilityNum);
            }

            //가까운 공원 10개 면적 순 정렬
            double[] areaArray = Area.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
            sortParkInfoByQuickSort(parkInfoArea, areaArray);
            Collections.reverse(parkInfoArea);

            // 점수 계산
            for(int i=0; i<10; i++){
                String array[] = parkInfoScore.get(i).split(",");
                Score.add(distanceArray[0] / Double.parseDouble(array[17]) * 75.0
                    + Double.parseDouble(array[7]) / areaArray[9] * 15.0
                    + facilityNum.get(i) / maxFacilityNum * 10.0);
            }

            //가까운 공원 10개 점수 순 정렬
            double[] scoreArray = Score.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
            sortParkInfoByQuickSort(parkInfoScore, scoreArray);
            Collections.reverse(parkInfoScore);

            parkInfoArray.add(parkInfoDistance);
            parkInfoArray.add(parkInfoArea);
            parkInfoArray.add(parkInfoScore);

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

        return parkInfoArray; // 거리 순, 면적 순, 추천 순으로 정렬된 리스트 반환
    }

    // 공원 정보 정렬
    public static void sortParkInfoByQuickSort(ArrayList parkInfo, double[] arr) {
        quickSort(parkInfo, arr, 0, arr.length - 1);
    }
    public static void quickSort(ArrayList parkInfo, double[] arr, int left, int right) {
        int part = partition(parkInfo, arr, left, right);
        if (left < part - 1) {
            quickSort(parkInfo, arr, left, part - 1);
        }
        if (part < right) {
            quickSort(parkInfo, arr, part, right);
        }
    }
    public static int partition(ArrayList parkInfo, double[] arr, int left, int right) {
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

    private static void swap(ArrayList parkInfo, double[] arr, int left, int right) {
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