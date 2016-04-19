package site.elioplasma.ecook.grocerylist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PlaceActivity extends SingleFragmentActivity {

    private static final String EXTRA_PLACE_ID =
            "site.elioplasma.ecook.grocerylist.place_id";

    public static Intent newIntent(Context packageContext, String placeId) {
        Intent intent = new Intent(packageContext, PlaceActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, placeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String placeId = getIntent()
                .getStringExtra(EXTRA_PLACE_ID);
        return PlaceFragment.newInstance(placeId);
    }
}
