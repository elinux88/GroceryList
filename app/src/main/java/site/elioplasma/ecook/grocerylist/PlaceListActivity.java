package site.elioplasma.ecook.grocerylist;

import android.support.v4.app.Fragment;

public class PlaceListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PlaceListFragment();
    }
}
