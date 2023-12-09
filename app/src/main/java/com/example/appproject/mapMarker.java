package com.example.appproject;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class mapMarker implements OnMapReadyCallback {
    private GoogleMap mMap;
    private readfiles readfile;
    Resources res;
    private SupportMapFragment mapFragment;
    private String[] latlist, lonlist, area;
    private String str, resultFac, temp, year;
    private double tempUHI;
    private MarkerOptions[] MarkerOptionsList;
    private CircleOptions[] CircleOptionsList;
    Activity mainAct;

    public mapMarker(Resources res, SupportMapFragment mapFragment, readfiles readfile, Activity mainAct) {
        year = "2017";
        area = new String[19];
        this.mapFragment = mapFragment;
        mapFragment.getMapAsync(this);

        this.res = res;
        this.readfile = readfile;
        this.mainAct = mainAct;

        latlist = res.getStringArray(R.array.lat_list);
        lonlist = res.getStringArray(R.array.lon_list);
        area = res.getStringArray(R.array.area_list);

        MarkerOptionsList = new MarkerOptions[latlist.length];
        CircleOptionsList = new CircleOptions[latlist.length];
    }

    public void onMapReady(final GoogleMap googleMap){
        mMap = googleMap;
        LatLng JEONJU = new LatLng(35.8412, 127.1318);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(JEONJU,16));
        mMap.getUiSettings().setZoomControlsEnabled(true); //https://luminitworld.tistory.com/61

        ChecktheMarkers();
        setCircle();

        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(@NonNull Circle circle) {
                String circleid = circle.getId().toString().substring(2);
                int idToint = Integer.parseInt(circleid);
                idToint = idToint % 19;
                double idTodouble = readfile.getUHITemp(idToint);
                Toast.makeText(mainAct, String.format("%.2f", idTodouble), Toast.LENGTH_SHORT).show();

                LatLng newloc = new LatLng(Double.parseDouble(latlist[idToint]), Double.parseDouble(lonlist[idToint]));
                changeCamera(newloc);
            }
        });
    }

    public void changeCamera(LatLng newloc) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(newloc);
        mMap.animateCamera(cameraUpdate);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newloc,16));
    }

    public void changeYear(String Newyear) {
        if(year == Newyear) //같은 년도로 접근시 map 초기화X
            return;
        year = Newyear;

        mMap.clear();
        ChecktheMarkers();
        setCircle();
    }

    private void ChecktheMarkers() {
        for(int i = 0; i < latlist.length; i++) {
            double lat = Double.parseDouble(latlist[i]);
            double lon = Double.parseDouble(lonlist[i]);
            LatLng LOCATION = new LatLng(lat, lon);

            str = area[i];
            resultFac = str.substring(str.lastIndexOf(".")+1);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(LOCATION);
            markerOptions.title(resultFac);

            temp = readfile.getDatas(i);

            if(temp == "" || temp == "0")
                temp = "해당 년도의 데이터가 존재하지 않습니다";

            markerOptions.snippet(temp);

            MarkerOptionsList[i] = markerOptions;
            mMap.addMarker(markerOptions);
        }
    }

    private void setCircle() {
        for(int i = 0; i < latlist.length; i++) {
            double lat = Double.parseDouble(latlist[i]);
            double lon = Double.parseDouble(lonlist[i]);
            LatLng LOCATION = new LatLng(lat, lon);

            CircleOptions circle1km = new CircleOptions().center(LOCATION)
                    .radius(500)
                    .fillColor(getColor(i))
                    .strokeWidth(0f)
                    .clickable(true);

            CircleOptionsList[i] = circle1km;

            mMap.addCircle(circle1km);
        }
    }

    private int getColor(int index) {
        tempUHI = readfile.getUHITemp(index);
        if(tempUHI >= 81)
            return 0x80FF0000;
        if(tempUHI >= 76 && tempUHI < 81)
            return 0x80FF6600;
        if(tempUHI >= 71 && tempUHI < 76)
            return 0x80FF9900;

        if(tempUHI == 0.0)
            return 0x80CCCCCC;

        return 0x80FFFF33;
    }
}
