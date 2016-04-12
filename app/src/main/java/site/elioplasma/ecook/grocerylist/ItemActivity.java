package site.elioplasma.ecook.grocerylist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class ItemActivity extends SingleFragmentActivity {

    private static final String EXTRA_ITEM_ID =
            "site.elioplasma.ecook.grocerylist.item_id";

    public static Intent newIntent(Context packageContext, UUID itemId) {
        Intent intent = new Intent(packageContext, ItemActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID itemId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_ITEM_ID);
        return ItemFragment.newInstance(itemId);
    }
}
