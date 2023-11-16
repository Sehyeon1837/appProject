package parkMap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.appproject.R;

import java.util.ArrayList;

public class park_List_View extends AppCompatActivity {
    Button closeButton;
    private String latitude;
    private String longitude;
    private ListView listView;
    private parkListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_list_view);

        ArrayList<String> parkInfo = (ArrayList<String>)getIntent().getSerializableExtra("parkInfo");
        this.longitude = parkInfo.get(parkInfo.size()-1); parkInfo.remove(parkInfo.size()-1);
        this.latitude = parkInfo.get(parkInfo.size()-1); parkInfo.remove(parkInfo.size()-1);
        closeButton = findViewById(R.id.closeBtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new parkListViewAdapter(latitude, longitude);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        for(int i=0; i<10; i++){
            String info = parkInfo.get(i);
            adapter.addItem(info);
        }
        adapter.notifyDataSetChanged();
    }
}