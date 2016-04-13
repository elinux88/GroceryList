package site.elioplasma.ecook.grocerylist.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import site.elioplasma.ecook.grocerylist.Item;
import site.elioplasma.ecook.grocerylist.database.ItemDbSchema.ItemTable;

/**
 * Created by eli on 4/12/16.
 */
public class ItemCursorWrapper extends CursorWrapper {
    public ItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Item getItem() {
        String uuidString = getString(getColumnIndex(ItemTable.Cols.UUID));
        String name = getString(getColumnIndex(ItemTable.Cols.NAME));
        int amount = getInt(getColumnIndex(ItemTable.Cols.AMOUNT));

        Item item = new Item(UUID.fromString(uuidString));
        item.setName(name);
        item.setAmount(amount);

        return item;
    }
}
