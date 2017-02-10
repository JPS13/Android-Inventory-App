package udacity.android.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents an item in a business inventory.
 *
 * @author Joseph Stewart
 * @version 1.0
 */
public class Item implements Parcelable {

    private long id;
    private String description;
    private double price;
    private int quantity;
    private String supplierEmail;
    private String image;

    public Item() {

    }

    public Item(String description, double price, int quantity, String supplierEmail, String image) {
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.supplierEmail = supplierEmail;
        this.image = image;
    }

    public Item(long id, String description, double price, int quantity, String supplierEmail, String image) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.supplierEmail = supplierEmail;
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Description: " + description + ", Price: " + price
                + ", Quantity: " + quantity + ", Supplier Email: " + supplierEmail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeString(supplierEmail);
        dest.writeString(image);
    }

    public static final Parcelable.Creator<Item> CREATOR
            = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        id = in.readLong();
        description = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
        supplierEmail = in.readString();
        image = in.readString();
    }
}
