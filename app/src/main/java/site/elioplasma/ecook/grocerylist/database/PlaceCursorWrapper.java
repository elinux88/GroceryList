package site.elioplasma.ecook.grocerylist.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import site.elioplasma.ecook.grocerylist.StorePlace;
import site.elioplasma.ecook.grocerylist.database.PlaceDbSchema.PlaceTable;

/**
 * Created by eli on 4/12/16.
 */
public class PlaceCursorWrapper extends CursorWrapper {
    public PlaceCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public StorePlace getPlace() {
        String id = getString(getColumnIndex(PlaceTable.Cols.ID));
        CharSequence name = getString(getColumnIndex(PlaceTable.Cols.NAME));
        CharSequence address = getString(getColumnIndex(PlaceTable.Cols.ADDRESS));

        StorePlace place = new StorePlace(id);
        place.setName(name);
        place.setAddress(address);

        return place;
    }
}
