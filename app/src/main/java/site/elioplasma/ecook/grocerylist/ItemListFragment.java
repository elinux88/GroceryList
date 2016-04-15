package site.elioplasma.ecook.grocerylist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by eli on 3/2/16.
 */
public class ItemListFragment extends Fragment {

    private RecyclerView mItemRecyclerView;
    private ItemAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        mItemRecyclerView = (RecyclerView) view
                .findViewById(R.id.item_recycler_view);
        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_item_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_new_item:
                Item item = new Item();
                ItemData.get(getActivity()).addItem(item);
                Intent intent = ItemActivity
                        .newIntent(getActivity(), item.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void updateUI() {
        ItemData itemData = ItemData.get(getActivity());
        List<Item> items = itemData.getItems();

        if (mAdapter == null) {
            mAdapter = new ItemAdapter(items);
            mItemRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Item mItem;

        private TextView mNameTextView;
        private TextView mAmountTextView;

        public ItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mNameTextView = (TextView)
                    itemView.findViewById(R.id.list_item_item_name_text_view);
            mAmountTextView = (TextView)
                    itemView.findViewById(R.id.list_item_item_amount_text_view);
        }

        public void bindItem(Item item) {
            mItem = item;
            mNameTextView.setText(mItem.getName());
            mAmountTextView.setText(Integer.toString(mItem.getAmount()));
        }

        @Override
        public void onClick(View v) {
            Intent intent = ItemActivity.newIntent(getActivity(), mItem.getId());
            String transitionName = getString(R.string.shared_item_name);
            startWithTransition(getActivity(), intent, v, transitionName);
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {

        private List<Item> mItems;

        public ItemAdapter(List<Item> items) {
            mItems = items;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            Item item = mItems.get(position);
            holder.bindItem(item);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void setItems(List<Item> items) {
            mItems = items;
        }
    }

    private static void startWithTransition(Activity activity, Intent intent,
                                            View sourceView, String transitionName) {

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, sourceView, transitionName);

        activity.startActivity(intent, options.toBundle());
    }
}
