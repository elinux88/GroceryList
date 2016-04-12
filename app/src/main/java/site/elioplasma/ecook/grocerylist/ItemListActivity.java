package site.elioplasma.ecook.grocerylist;

import android.support.v4.app.Fragment;

public class ItemListActivity extends SingleFragmentActivity {

    private static final String TAG = "GroceryList";

    @Override
    protected Fragment createFragment() {
        return new ItemListFragment();
    }
}
