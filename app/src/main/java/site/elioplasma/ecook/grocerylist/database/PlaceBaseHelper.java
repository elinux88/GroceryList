package site.elioplasma.ecook.grocerylist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import site.elioplasma.ecook.grocerylist.database.PlaceDbSchema.PlaceTable;

/**
 * Created by eli on 4/12/16.
 */
public class PlaceBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static String DATABASE_NAME = "placeBase.db";

    public PlaceBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PlaceTable.TABLE_NAME + "(" +
                " _id integer primary key autoincrement, " +
                PlaceTable.Cols.ID + ", " +
                PlaceTable.Cols.NAME + ", " +
                PlaceTable.Cols.ADDRESS +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
