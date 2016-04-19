package site.elioplasma.ecook.grocerylist;

import java.util.UUID;

/**
 * Created by eli on 4/19/16.
 */
public class StorePlace {

    private String mId;
    private CharSequence mName;
    private CharSequence mAddress;

    public StorePlace() {
        this(UUID.randomUUID().toString());
    }

    public StorePlace(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public CharSequence getName() {
        return mName;
    }

    public void setName(CharSequence name) {
        mName = name;
    }

    public CharSequence getAddress() {
        return mAddress;
    }

    public void setAddress(CharSequence address) {
        mAddress = address;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId() + ".jpg";
    }
}
