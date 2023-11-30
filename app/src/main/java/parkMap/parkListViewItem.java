package parkMap;

public class parkListViewItem {
    private String iconDrawble;
    private String parkName;
    private String parkArea;
    private String parkDistance;
    private String parkAddress;
    private String parkFacility;
    private String parkPhoneNumber;
    private String latitude;
    private String longitude;

    public void setParkName(String parkName){
        this.parkName = parkName;
    }
    public void setParkArea(String parkArea){
        this.parkArea = parkArea+"m";
    }
    public void setParkDistance(String parkDistance) {this.parkDistance = parkDistance+"m"; }
    public void setParkAddress(String parkAddress) { this.parkAddress = parkAddress; }
    public void setParkFacility(String parkFacility) { this.parkFacility = parkFacility; }
    public void setParkPhoneNumber(String parkPhoneNumber) {this.parkPhoneNumber = parkPhoneNumber; }
    public void setLatitude(String latitude) {this.latitude = latitude;}
    public void setLongitude(String longitude){this.longitude = longitude;}
    public void setIcon(String iconRes) {
        iconDrawble = iconRes;
    }

    public String getParkName() {return this.parkName;}
    public String getParkArea() {return this.parkArea;}
    public String getParkDistance(){ return this.parkDistance;}
    public String getParkAddress() {return this.parkAddress;}
    public String getParkFacility() {return this.parkFacility;}
    public String getParkPhoneNumber() {return this.parkPhoneNumber;}
    public String getLatitude() {return this.latitude;}
    public String getLongitude(){return this.longitude;}
    public String getIcon(){
        return this.iconDrawble;
    }
}