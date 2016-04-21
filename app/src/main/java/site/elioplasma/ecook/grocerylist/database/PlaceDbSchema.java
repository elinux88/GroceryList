package site.elioplasma.ecook.grocerylist.database;

/**
 * Created by eli on 4/12/16.
 */
public class PlaceDbSchema {
    public static final class PlaceTable {
        public static final String TABLE_NAME = "places";

        public static final class Cols {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String ADDRESS = "address";
            public static final String ATTRIBUTIONS = "attributions";
            public static final String PHOTO_TYPE = "photo_type";
        }
    }
}
