package il.co.nnz.myplaces;

/**
 * Created by User on 08/06/2016.
 */
public class Place {

    private String placeID, name, address, lat, lng, icon, phone, website;
    private int image;
    private long id;



    public Place(long id, String name, String address, String lat, String lng) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lng = lng;
        this.lat = lat;
    }

    public Place( String placeID, String name, String address, String lat, String lng) {
        this.placeID = placeID;
        this.lng = lng;
        this.lat = lat;
        this.address = address;
        this.name = name;
    }


    public Place(String placeID, String name, String address, String lat, String lng, String icon) {

        this.placeID = placeID;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.icon=icon;
    }

    public Place(long id, String name, String address, String lat, String lng, String icon) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.icon=icon;
    }

    public Place(String placeID, String name, String address, String lat, String lng, String icon, String phone, String website) {

        this.placeID = placeID;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.icon=icon;
        this.phone=phone;
        this.website=website;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public long getId() { return id; }

    public String getIcon() {
        return icon;
    }
}
