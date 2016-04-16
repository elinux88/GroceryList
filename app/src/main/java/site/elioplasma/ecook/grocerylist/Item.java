package site.elioplasma.ecook.grocerylist;

import java.util.UUID;

/**
 * Created by eli on 2/4/16.
 */
public class Item {

    public static final int PHOTO_DEFAULT = 0;
    public static final int PHOTO_CAMERA = 1;
    public static final int PHOTO_INTERNET = 2;

    private UUID mId;
    private String mName;
    private int mAmount;
    private int mPhotoType;

    public Item() {
        this(UUID.randomUUID());
    }

    public Item(UUID id) {
        mId = id;
        mAmount = 1;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public int getPhotoType() {
        return mPhotoType;
    }

    public void setPhotoType(int photoType) {
        mPhotoType = photoType;
    }
}
