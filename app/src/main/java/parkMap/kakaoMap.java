package parkMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.appproject.R;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class kakaoMap extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener{
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private Double latitude;
    private Double longitude;
    private Button closeButton;
    private Button listButton;
    private Spinner spinner;
    private ArrayAdapter spnAdapter;
    private ArrayList<String> parkInfo;
    private MarkerEventListener markerEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_map);

        //지도 띄우기
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        mapView.setPOIItemEventListener(markerEventListener = new MarkerEventListener());

        ArrayList<ArrayList> parkInfoArray = (ArrayList<ArrayList>)getIntent().getSerializableExtra("parkInfo"); // 거리 순, 면적 순, 추천 순으로 정렬 된 공원 정보
        ArrayList<String> parkInfoDistance = parkInfoArray.get(0); // 거리 순으로 정렬 된 공원 정보
        ArrayList<String> parkInfoArea = parkInfoArray.get(1); // 면적 순으로 정렬 된 공원 정보
        ArrayList<String> parkInfoScore = parkInfoArray.get(2); // 추천 순으로 정렬 된 공원 정보
        this.latitude = Double.parseDouble((String) parkInfoArray.get(parkInfoArray.size()-1).get(0));
        this.longitude = Double.parseDouble((String) parkInfoArray.get(parkInfoArray.size()-1).get(1));
        parkInfoArray.remove(parkInfoArray.size()-1);
        parkInfo = parkInfoDistance;

        MapPOIItem[] markers = new MapPOIItem[10];
        drawMarker(markers); // 가까운 공원 10개 마커 띄우기

        closeButton = findViewById(R.id.closeBtn);
        listButton = findViewById(R.id.listBtn);
        spinner = (Spinner) findViewById(R.id.spinner);
        spnAdapter = ArrayAdapter.createFromResource(this, R.array.spinnerItems, android.R.layout.simple_spinner_item);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spnAdapter);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), park_List_View.class);
                ArrayList<String> array = new ArrayList<String>();
                array.add(Double.toString(latitude));
                array.add(Double.toString(longitude));
                array.add(Integer.toString(spinner.getSelectedItemPosition()));
                parkInfoArray.add(array);
                intent.putExtra("parkInfo", parkInfoArray);
                startActivity(intent); // 리스트 뷰 띄우기
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinner.getItemAtPosition(position).toString();
                if(selected.equals("거리순")) parkInfo = parkInfoDistance;
                else if(selected.equals("면적순")) parkInfo = parkInfoArea;
                else if(selected.equals("추천순")) parkInfo = parkInfoScore;
                if(markers != null)mapView.removePOIItems(markers);
                drawMarker(markers);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        }
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    // 입력 받은 공원 정보로 마커 띄우기
    public void drawMarker(MapPOIItem[] markers){
        for (int i=0; i<10; i++) {
            markers[i] = new MapPOIItem();
            String array[] = parkInfo.get(i).split(",");
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(array[5]), Double.parseDouble(array[6]));
            markers[i].setItemName(parkInfo.get(i));
            markers[i].setMapPoint(mapPoint);
            if (i<3) markers[i].setMarkerType(MapPOIItem.MarkerType.YellowPin); // 상위 3곳은 기본으로 제공하는 YellowPin 마커 모양
            else markers[i].setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 RedPin 마커 모양.
            markers[i].setSelectedMarkerType(MapPOIItem.MarkerType.BluePin); // 마커를 클릭 했을 때, 기본으로 제공하는 BluePin 마커 모양.
            mapView.addPOIItem(markers[i]);
        }

        mapView.selectPOIItem(markers[0], true);
    }

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;
        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.kakaomap_balloon_view, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            String array[] = poiItem.getItemName().split(",");
            ((TextView) mCalloutBalloon.findViewById(R.id.parkName)).setText(array[1]);
            ((TextView) mCalloutBalloon.findViewById(R.id.parkArea)).setText(array[7]);
            ((TextView) mCalloutBalloon.findViewById(R.id.parkDistance)).setText(array[17] + "m");
            ((TextView) mCalloutBalloon.findViewById(R.id.parkAddress)).setText(array[3]);
            ((TextView) mCalloutBalloon.findViewById(R.id.parkFacility)).setText(array[8] + "\n" + array[9] + "\n" + array[10] + "\n" + array[11] + "\n" + array[12]);
            ((TextView) mCalloutBalloon.findViewById(R.id.parkPhoneNumber)).setText(array[15]);

            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return mCalloutBalloon;
        }
    }

    class MarkerEventListener implements MapView.POIItemEventListener{
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
            String[] array = mapPOIItem.getItemName().split(",");
            String url = "kakaomap://search?q=" + array[1] + "&p=" + latitude + "," + longitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    }
}