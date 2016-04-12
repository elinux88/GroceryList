package site.elioplasma.ecook.grocerylist;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by eli on 3/2/16.
 */
public class ItemData {
    private static ItemData sItemData;

    private List<Item> mItems;
    private Context mContext;

    public static ItemData get(Context context) {
        if (sItemData == null) {
            sItemData = new ItemData(context);
        }
        return sItemData;
    }

    private ItemData(Context context) {
        mContext = context.getApplicationContext();
        mItems = new ArrayList<>();
        addItemFull("Cabbage", 2);
        addItemFull("Cheese", 4);
        addItemFull("Apple", 3);
        addItemFull("Salsa", 1);
        addItemFull("Bread", 7);
        addItemFull("Egg", 2);
        addItemFull("Watermelon", 1);
        addItemFull("Lime", 9);
        addItemFull("Bag O' Chips", 2);
        addItemFull("Spinach", 5);
        addItemFull("Cat food", 3);
    }

    public void addItem(Item i) {
        mItems.add(i);
    }

    public File getPhotoFile(Item item) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, item.getPhotoFilename());
    }

    private void addItemFull(String name, int amount) {
        Item item = new Item();
        item.setName(name);
        item.setAmount(amount);
        mItems.add(item);
    }

    public List<Item> getItems() {
        return mItems;
    }

    public Item getItem(UUID id) {
        for (Item item : mItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public boolean deleteItem(UUID id) {
        for (Item item : mItems) {
            if (item.getId().equals(id)) {
                mItems.remove(item);
                return true;
            }
        }
        return false;
    }
}
