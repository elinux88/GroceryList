package site.elioplasma.ecook.grocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import site.elioplasma.ecook.grocerylist.database.ItemBaseHelper;
import site.elioplasma.ecook.grocerylist.database.ItemCursorWrapper;
import site.elioplasma.ecook.grocerylist.database.ItemDbSchema.ItemTable;

/**
 * Created by eli on 3/2/16.
 */
public class ItemData {
    private static ItemData sItemData;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ItemData get(Context context) {
        if (sItemData == null) {
            sItemData = new ItemData(context);
        }
        return sItemData;
    }

    private ItemData(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ItemBaseHelper(mContext)
                .getWritableDatabase();

        ItemCursorWrapper cursor = queryItems(null, null);
        try {
            if (cursor.getCount() == 0) {
                initItemList();
            }
        } finally {
            cursor.close();
        }
    }

    public void addItem(Item i) {
        ContentValues values = getContentValues(i);

        mDatabase.insert(ItemTable.TABLE_NAME, null, values);
    }

    public File getPhotoFile(Item item) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, item.getPhotoFilename());
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();

        ItemCursorWrapper cursor = queryItems(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursor.getItem());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public Item getItem(UUID id) {
        ItemCursorWrapper cursor = queryItems(
                ItemTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getItem();
        } finally {
            cursor.close();
        }
    }

    public void updateItem(Item item) {
        String uuidString = item.getId().toString();
        ContentValues values = getContentValues(item);

        mDatabase.update(ItemTable.TABLE_NAME, values,
                ItemTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public boolean deleteItem(UUID id) {
        mDatabase.delete(ItemTable.TABLE_NAME,
                ItemTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
        return true;
    }

    private static ContentValues getContentValues(Item item) {
        ContentValues values = new ContentValues();
        values.put(ItemTable.Cols.UUID, item.getId().toString());
        values.put(ItemTable.Cols.NAME, item.getName());
        values.put(ItemTable.Cols.AMOUNT, Integer.toString(item.getAmount()));

        return values;
    }

    private ItemCursorWrapper queryItems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ItemTable.TABLE_NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new ItemCursorWrapper(cursor);
    }

    private void initItemList() {
        String[] itemNames = {
                "Cabbage",
                "Cheese",
                "Apple",
                "Salsa",
                "Bread",
                "Egg",
                "Watermelon",
                "Lime",
                "Bag O' Chips",
                "Spinach",
                "Cat food"
        };
        int[] itemAmounts = {
                2, 4, 3, 1, 7,
                2, 1, 9, 2, 5,
                3
        };
        for (int i = 0; i < itemNames.length; i++) {
            Item item = new Item();
            item.setName(itemNames[i]);
            item.setAmount(itemAmounts[i]);
            addItem(item);
        }
    }
}
