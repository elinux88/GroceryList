package site.elioplasma.ecook.grocerylist;

import java.util.UUID;

/**
 * Created by eli on 4/19/16.
 */
public class StorePlace {

    public static final int PHOTO_DEFAULT = 0;
    public static final int PHOTO_CAMERA = 1;
    public static final int PHOTO_INTERNET = 2;

    private String mId;
    private String mName;
    private String mAddress;
    private String mAttributions;
    private int mPhotoType;

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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getAttributions() {
        return mAttributions;
    }

    public void setAttributions(String attributions) {
        mAttributions = attributions;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId() + ".jpg";
    }

    public int getPhotoType() {
        return mPhotoType;
    }

    public void setPhotoType(int photoType) {
        mPhotoType = photoType;
    }
}
