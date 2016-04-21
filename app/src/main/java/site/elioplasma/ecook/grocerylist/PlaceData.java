package site.elioplasma.ecook.grocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

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
        File filesDir = null;

        switch (storePlace.getPhotoType()) {
            case StorePlace.PHOTO_INTERNET:
                filesDir = mContext.getFilesDir();
                break;
            default:
                filesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }

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
        values.put(PlaceTable.Cols.NAME, storePlace.getName());
        values.put(PlaceTable.Cols.ADDRESS, storePlace.getAddress());
        values.put(PlaceTable.Cols.ATTRIBUTIONS, storePlace.getAttributions());
        values.put(PlaceTable.Cols.PHOTO_TYPE, Integer.toString(storePlace.getPhotoType()));

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
        String[] placeNames = {
                "Harmons Neighborhood Grocer",
                "Walmart Supercenter",
                "Albertsons",
        };
        String[] placeAddresses = {
                "1189 E 700 S, St George, UT 84790, United States",
                "625 W Telegraph St, Washington, UT 84780, United States",
                "915 Red Cliffs Dr, Washington, UT 84780, United States"
        };
        String[] placeAttributions = {"", "", "",};
        for (int i = 0; i < placeNames.length; i++) {
            StorePlace storePlace = new StorePlace();
            storePlace.setName(placeNames[i]);
            storePlace.setAddress(placeAddresses[i]);
            storePlace.setAttributions(placeAttributions[i]);
            addPlace(storePlace);
        }
    }
}
