package site.elioplasma.ecook.grocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import site.elioplasma.ecook.grocerylist.database.PlaceBaseHelper;
import site.elioplasma.ecook.grocerylist.database.PlaceCursorWrapper;
import site.elioplasma.ecook.grocerylist.database.PlaceDbSchema.PlaceTable;

/**
 * Created by eli on 3/2/16.
 */
public class PlaceData {
    private static final String TAG = "PlaceData";
    private static PlaceData sPlaceData;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static PlaceData get(Context context) {
        if (sPlaceData == null) {
            sPlaceData = new PlaceData(context);
        }
        return sPlaceData;
    }

    private PlaceData(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new PlaceBaseHelper(mContext)
                .getWritableDatabase();

        PlaceCursorWrapper cursor = queryPlaces(null, null);
        try {
            if (cursor.getCount() == 0) {
                initPlaceList();
            }
        } finally {
            cursor.close();
        }
    }

    public void addPlace(StorePlace sp) {
        ContentValues values = getContentValues(sp);

        mDatabase.insert(PlaceTable.TABLE_NAME, null, values);
    }

    public File getPhotoFile(StorePlace storePlace) {
        File filesDir = mContext.getFilesDir();

        if (filesDir == null) {
            return null;
        }

        return new File(filesDir, storePlace.getPhotoFilename());
    }

    public List<StorePlace> getPlaces() {
        List<StorePlace> places = new ArrayList<>();

        PlaceCursorWrapper cursor = queryPlaces(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                places.add(cursor.getPlace());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return places;
    }

    public StorePlace getPlace(String id) {
        PlaceCursorWrapper cursor = queryPlaces(
                PlaceTable.Cols.ID + " = ?",
                new String[]{id}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getPlace();
        } finally {
            cursor.close();
        }
    }

    public void updatePlace(StorePlace storePlace) {
        String id = storePlace.getId();
        ContentValues values = getContentValues(storePlace);

        mDatabase.update(PlaceTable.TABLE_NAME, values,
                PlaceTable.Cols.ID + " = ?",
                new String[]{id});
    }

    public boolean deletePlace(String id) {
        mDatabase.delete(PlaceTable.TABLE_NAME,
                PlaceTable.Cols.ID + " = ?",
                new String[]{id});
        return true;
    }

    private static ContentValues getContentValues(StorePlace storePlace) {
        ContentValues values = new ContentValues();
        values.put(PlaceTable.Cols.ID, storePlace.getId());
        values.put(PlaceTable.Cols.NAME, (String) storePlace.getName());
        values.put(PlaceTable.Cols.ADDRESS, (String) storePlace.getAddress());

        return values;
    }

    private PlaceCursorWrapper queryPlaces(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PlaceTable.TABLE_NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new PlaceCursorWrapper(cursor);
    }

    private void initPlaceList() {
        CharSequence[] placeNames = {
                "Harmons",
                "Walmart",
                "Albertsons",
        };
        CharSequence[] placeAddresses = {
                "1180 E 700 S",
                "625 W Telegraph St",
                "915 Red Cliffs Dr"
        };
        for (int i = 0; i < placeNames.length; i++) {
            StorePlace storePlace = new StorePlace();
            storePlace.setName(placeNames[i]);
            storePlace.setAddress(placeAddresses[i]);
            addPlace(storePlace);
        }
    }
}
