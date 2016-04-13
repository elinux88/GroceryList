package site.elioplasma.ecook.grocerylist;

import java.util.UUID;

/**
 * Created by eli on 2/4/16.
 */
public class Item {

    private UUID mId;
    private String mName;
    private int mAmount;

    public Item() {
        this(UUID.randomUUID());
    }

    public Item(UUID id) {
        mId = id;
        mName = "New Item";
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
}
