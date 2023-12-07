package parkMap;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appproject.R;

public class Holder {
    public LinearLayout listViewItem;
    public ImageView parkImage;
    public TextView parkName;
    public TextView parkArea;
    public TextView parkDistance;
    public TextView parkAddress;
    public TextView parkFacility;
    public TextView parkPhoneNumber;
    public Button naviBtn;

    public Holder(View root){
        listViewItem = (LinearLayout) root.findViewById(R.id.listViewItem);
        parkImage = (ImageView) root.findViewById(R.id.parkImage);
        parkName = (TextView) root.findViewById(R.id.parkName);
        parkArea = (TextView) root.findViewById(R.id.parkArea);
        parkDistance = (TextView) root.findViewById(R.id.parkDistance);
        parkAddress = (TextView) root.findViewById(R.id.parkAddress);
        parkFacility = (TextView) root.findViewById(R.id.parkFacility);
        parkPhoneNumber = (TextView) root.findViewById(R.id.parkPhoneNumber);
        naviBtn = (Button) root.findViewById(R.id.naviBtn);
    }

}
