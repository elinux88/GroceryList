package site.elioplasma.ecook.grocerylist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import site.elioplasma.ecook.grocerylist.database.ItemDbSchema.ItemTable;

/**
 * Created by eli on 4/12/16.
 */
public class ItemBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static String DATABASE_NAME = "itemBase.db";

    public ItemBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ItemTable.TABLE_NAME + "(" +
                " _id integer primary key autoincrement, " +
                ItemTable.Cols.UUID + ", " +
                ItemTable.Cols.NAME + ", " +
                ItemTable.Cols.AMOUNT +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
