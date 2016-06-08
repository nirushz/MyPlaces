package il.co.nnz.myplaces;

/**
 * Created by User on 08/06/2016.
 */
public class Place {

    String name, address;
    double distance;
    int image;

    public Place(String name, String address, double distance) {
        this.name = name;
        this.address = address;
        this.distance = distance;
    }

    public Place(String name, String address, double distance, int image) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.image = image;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
