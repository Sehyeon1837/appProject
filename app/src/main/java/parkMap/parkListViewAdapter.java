package parkMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.appproject.R;
import java.util.ArrayList;


public class parkListViewAdapter extends BaseAdapter {
    private Double latitude;
    private Double longitude;
    private LinearLayout listViewItem;
    private ImageView parkImage;
    private TextView parkName;
    private TextView parkDistance;
    private TextView parkAddress;
    private TextView parkFacility;
    private TextView parkPhoneNumber;
    private Button naviBtn;
    private ArrayList<parkListViewItem> parkListViewItemArrayList = new ArrayList<parkListViewItem>();
    public parkListViewAdapter(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int getCount() {
        return parkListViewItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return parkListViewItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_park_list_view_item, parent, false);
        }

        listViewItem = (LinearLayout) convertView.findViewById(R.id.listViewItem);
        parkImage = (ImageView) convertView.findViewById(R.id.parkImage);
        parkName = (TextView) convertView.findViewById(R.id.parkName);
        parkDistance = (TextView) convertView.findViewById(R.id.parkDistance);
        parkAddress = (TextView) convertView.findViewById(R.id.parkAddress);
        parkFacility = (TextView) convertView.findViewById(R.id.parkFacility);
        parkPhoneNumber = (TextView) convertView.findViewById(R.id.parkPhoneNumber);
        naviBtn = (Button) convertView.findViewById(R.id.naviBtn);

        parkListViewItem parkListViewItem = parkListViewItemArrayList.get(position);

        parkName.setText(parkListViewItem.getParkName());
        parkDistance.setText(parkListViewItem.getParkDistance());
        parkAddress.setText(parkListViewItem.getParkAddress());
        parkFacility.setText(parkListViewItem.getParkFacility());
        parkPhoneNumber.setText(parkListViewItem.getParkPhoneNumber());

        naviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "kakaomap://route?sp=" + latitude + "," + longitude + "&ep=" + parkListViewItem.getLatitude() + "," + parkListViewItem.getLongitude() + "&by=FOOT";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });

        listViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "kakaomap://search?q=" + parkListViewItem.getParkName() + "&p=" + latitude + "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void addItem(String info){
        parkListViewItem item = new parkListViewItem();
        String array[] = info.split(",");
        item.setParkName(array[1]);
        item.setParkDistance(array[17]);
        item.setParkAddress(array[3]);
        item.setParkFacility(array[8] + "\n" + array[9] + "\n" + array[10] + "\n" + array[11] + "\n" + array[12]);
        item.setParkPhoneNumber(array[15]);
        item.setLatitude(array[5]);
        item.setLongitude(array[6]);

        parkListViewItemArrayList.add(item);
    }
}
