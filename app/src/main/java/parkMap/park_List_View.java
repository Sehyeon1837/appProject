package parkMap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.appproject.R;

import java.util.ArrayList;

public class park_List_View extends AppCompatActivity {
    private Button closeButton;
    private Spinner spinner;
    private Double latitude;
    private Double longitude;
    private ListView listView;
    private ArrayAdapter spnAdapter;
    private parkListViewAdapter adapter;
    private ArrayList<String> parkInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_list_view);

        ArrayList<ArrayList> parkInfoArray = (ArrayList<ArrayList>)getIntent().getSerializableExtra("parkInfo");
        ArrayList<String> parkInfoDistance = parkInfoArray.get(0); // 거리 순으로 정렬 된 공원 정보
        ArrayList<String> parkInfoArea = parkInfoArray.get(1); // 면적 순으로 정렬 된 공원 정보
        ArrayList<String> parkInfoScore = parkInfoArray.get(2); // 추천 순으로 정렬 된 공원 정보
        this.latitude = Double.parseDouble((String) parkInfoArray.get(parkInfoArray.size()-1).get(0));
        this.longitude = Double.parseDouble((String) parkInfoArray.get(parkInfoArray.size()-1).get(1));
        int selected = Integer.parseInt((String)parkInfoArray.get(parkInfoArray.size()-1).get(2));
        parkInfoArray.remove(parkInfoArray.size()-1);

        closeButton = findViewById(R.id.closeBtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spinner = (Spinner) findViewById(R.id.spinner);
        spnAdapter = ArrayAdapter.createFromResource(this, R.array.spinnerItems, android.R.layout.simple_spinner_item);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spnAdapter);
        spinner.setSelection(selected);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinner.getItemAtPosition(position).toString();
                if(selected.equals("거리순")) parkInfo = parkInfoDistance;
                else if(selected.equals("면적순")) parkInfo = parkInfoArea;
                else if(selected.equals("추천순")) parkInfo = parkInfoScore;
                adapter.resetItem();
                addListItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter = new parkListViewAdapter(latitude, longitude, getResources());
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    public void addListItem(){
        for(int i=0; i<10; i++){
            String info = parkInfo.get(i);
            adapter.addItem(info);
        }
        adapter.notifyDataSetChanged();
    }
}