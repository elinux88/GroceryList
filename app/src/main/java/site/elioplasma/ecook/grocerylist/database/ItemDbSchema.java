package site.elioplasma.ecook.grocerylist.database;

/**
 * Created by eli on 4/12/16.
 */
public class ItemDbSchema {
    public static final class ItemTable {
        public static final String TABLE_NAME = "items";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String AMOUNT = "amount";
            public static final String PHOTO_TYPE = "photo_type";
        }
    }
}
