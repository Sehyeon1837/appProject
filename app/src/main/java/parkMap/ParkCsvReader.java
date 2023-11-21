package parkMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.example.appproject.R;
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
    static Location location = new Location("User");
    public ParkCsvReader(Location userLocation){
        location = userLocation;
    }
    public static ArrayList<String> readCsv(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.jeonju_park_info);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        ArrayList<String> parkInfo = new ArrayList<String>();
        ArrayList<Double> Latitude = new ArrayList<Double>(); // 위도
        ArrayList<Double> Longitude = new ArrayList<Double>(); // 경도
        ArrayList<Double> Distance = new ArrayList<Double>(); // 사용자의 위치와 공원의 직선 거리
        String info = new String();
        Double distance;
        Location parkLocation = new Location("park");

        try {
            String line = "";
            info = br.readLine();
            while ((line = br.readLine()) != null) {
                String array[] = line.split(",");
                Latitude.add(Double.parseDouble(array[5])); // Latitude에 공원 위도 저장
                Longitude.add(Double.parseDouble(array[6])); // Longitude에 공원 경도 저장
                parkLocation.setLatitude(Latitude.get(Latitude.size()-1));
                parkLocation.setLongitude(Longitude.get(Longitude.size()-1));
                distance = (double)location.distanceTo(parkLocation);
                Distance.add(Math.floor(distance));
                line += "," + Distance.get(Distance.size()-1).intValue();
                parkInfo.add(line);
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
    public static void sortDistanceByQuickSort(ArrayList parkInfo, double[] arr) {
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