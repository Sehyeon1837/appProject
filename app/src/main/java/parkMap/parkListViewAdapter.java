package parkMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.appproject.R;

import org.apache.log4j.lf5.util.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class parkListViewAdapter extends BaseAdapter {
    private Double latitude;
    private Double longitude;
    private LinearLayout listViewItem;
    private ImageView parkImage;
    private TextView parkName;
    private TextView parkArea;
    private TextView parkDistance;
    private TextView parkAddress;
    private LinearLayout parkFacility;
    private LinearLayout parkFacility2;
    private TextView parkPhoneNumber;
    private Button naviBtn;
    private Resources resources;
    private ArrayList<parkListViewItem> parkListViewItemArrayList = new ArrayList<parkListViewItem>();
    public parkListViewAdapter(Double latitude, Double longitude, Resources resources){
        this.latitude = latitude;
        this.longitude = longitude;
        this.resources = resources;
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
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_park_list_view_item, parent, false);
            Holder holder = new Holder(convertView);
            convertView.setTag(holder);
        }

        Holder holder = (Holder) convertView.getTag();
        listViewItem = (LinearLayout) holder.listViewItem;
        parkImage = (ImageView) holder.parkImage;
        parkName = (TextView) holder.parkName;
        parkArea = (TextView) holder.parkArea;
        parkDistance = (TextView) holder.parkDistance;
        parkAddress = (TextView) holder.parkAddress;
        parkFacility = (LinearLayout) holder.parkFacility;
        parkFacility2 = (LinearLayout) holder.parkFacility2;
        parkPhoneNumber = (TextView) holder.parkPhoneNumber;
        naviBtn = (Button) holder.naviBtn;

        parkListViewItem parkListViewItem = parkListViewItemArrayList.get(position);

        parkImage.setImageResource(parkListViewItem.getIcon());
        parkName.setText(parkListViewItem.getParkName());
        parkArea.setText(parkListViewItem.getParkArea());
        parkDistance.setText(parkListViewItem.getParkDistance());
        parkAddress.setText(parkListViewItem.getParkAddress());
        parkPhoneNumber.setText(parkListViewItem.getParkPhoneNumber());
        parkFacility.removeAllViews();
        parkFacility2.removeAllViews();
        String[] ParkFacilityArray = parkListViewItem.getParkFacility();
        int facilityLength = 0;
        for(int i=0; i<ParkFacilityArray.length; i++){
            String facility = ParkFacilityArray[i];
            if(facility.length() != 0){
                facilityLength += facility.length();
                CardView cardView = new CardView(context);
                cardView.setRadius(10);
                cardView.setContentPadding(5, 5, 5, 5);
                TextView textView = new TextView(context);
                textView.setText(facility);
                textView.setTextSize(10);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setId(0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 15;
                cardView.setLayoutParams(params);
                cardView.addView(textView);
                if(facilityLength < 22) parkFacility.addView(cardView);
                else parkFacility2.addView(cardView);
            }
        }

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
        item.setParkArea(array[7]);
        item.setParkDistance(array[17]);
        item.setParkAddress(array[3]);
        item.setParkFacility(array[8] + "+" + array[9] + "+" + array[10] + "+" + array[11] + "+" + array[12]);
        item.setParkPhoneNumber(array[15]);
        item.setLatitude(array[5]);
        item.setLongitude(array[6]);
        item.setIcon(array[18], resources);
        parkListViewItemArrayList.add(item);
    }

    public void resetItem(){
        parkListViewItem item = new parkListViewItem();
        parkListViewItemArrayList.clear();
    }
}
