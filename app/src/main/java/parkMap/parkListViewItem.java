package parkMap;

import static java.sql.Types.NULL;

import android.content.res.Resources;

public class parkListViewItem {
    private Integer iconId;
    private String parkName;
    private String parkArea;
    private String parkDistance;
    private String parkAddress;
    private String[] parkFacility;
    private String parkPhoneNumber;
    private String latitude;
    private String longitude;

    public void setParkName(String parkName){
        this.parkName = parkName;
    }
    public void setParkArea(String parkArea){
        this.parkArea = parkArea+"m^2";
    }
    public void setParkDistance(String parkDistance) {
        Integer distance = Integer.parseInt(parkDistance);
        if(distance >= 1000){this.parkDistance = (distance/1000) + "." + ((distance - ((distance/1000) * 1000))/100) + "km | ";}
        else this.parkDistance = parkDistance+"m | ";
    }
    public void setParkAddress(String parkAddress) { this.parkAddress = parkAddress; }
    public void setParkFacility(String parkFacility) {
        String[] array = parkFacility.split("\\+");
        this.parkFacility = array;
    }
    public void setParkPhoneNumber(String parkPhoneNumber) {this.parkPhoneNumber = parkPhoneNumber; }
    public void setLatitude(String latitude) {this.latitude = latitude;}
    public void setLongitude(String longitude){this.longitude = longitude;}
    public void setIcon(String iconRes, Resources resources) {
        int index = Integer.parseInt(iconRes);
        String src = "park00";
        if(index<10) src += "00" + index;
        else if(index<100) src += "0" + index;
        else src += index;
        int id = resources.getIdentifier(src, "drawable", "com.example.appproject");
        if(id == NULL) this.iconId = resources.getIdentifier("@drawable/park00000", "drawable", "com.example.appproject" );
        else this.iconId = resources.getIdentifier(src, "drawable", "com.example.appproject" );
    }

    public String getParkName() {return this.parkName;}
    public String getParkArea() {return this.parkArea;}
    public String getParkDistance(){return this.parkDistance;}
    public String getParkAddress() {return this.parkAddress;}
    public String[] getParkFacility() {return this.parkFacility;}
    public String getParkPhoneNumber() {return this.parkPhoneNumber;}
    public String getLatitude() {return this.latitude;}
    public String getLongitude(){return this.longitude;}
    public Integer getIcon(){return this.iconId;}
}