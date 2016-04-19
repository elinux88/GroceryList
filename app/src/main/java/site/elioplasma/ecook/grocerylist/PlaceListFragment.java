package site.elioplasma.ecook.grocerylist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

/**
 * Created by eli on 3/2/16.
 */
public class PlaceListFragment extends Fragment {

    private static final String TAG = "PlaceList";
    private static final int PLACE_PICKER_REQUEST = 0;

    private RecyclerView mPlaceRecyclerView;
    private PlaceAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_list, container, false);

        mPlaceRecyclerView = (RecyclerView) view
                .findViewById(R.id.place_recycler_view);
        mPlaceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        inflater.inflate(R.menu.fragment_place_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_new_place:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_item_view_items:
                Intent i = new Intent(getActivity(), ItemListActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void updateUI() {
        PlaceData placeData = PlaceData.get(getActivity());
        List<StorePlace> storePlaces = placeData.getPlaces();

        if (mAdapter == null) {
            mAdapter = new PlaceAdapter(storePlaces);
            mPlaceRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setPlaces(storePlaces);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class PlaceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private StorePlace mPlace;

        private TextView mNameTextView;
        private TextView mAddressTextView;

        public PlaceHolder(View placeView) {
            super(placeView);
            placeView.setOnClickListener(this);

            mNameTextView = (TextView)
                    placeView.findViewById(R.id.list_item_place_name_text_view);
            mAddressTextView = (TextView)
                    placeView.findViewById(R.id.list_item_place_address_text_view);
        }

        public void bindPlace(StorePlace storePlace) {
            mPlace = storePlace;
            mNameTextView.setText(mPlace.getName());
            mAddressTextView.setText(mPlace.getAddress());
        }

        @Override
        public void onClick(View v) {
            Intent intent = PlaceActivity.newIntent(getActivity(), mPlace.getId());
            TextView nameView = (TextView) v.findViewById(R.id.list_item_place_name_text_view);
            String transitionName = getString(R.string.shared_place_name);

            startWithTransition(getActivity(), intent, nameView, transitionName);
        }
    }

    private class PlaceAdapter extends RecyclerView.Adapter<PlaceHolder> {

        private List<StorePlace> mPlaces;

        public PlaceAdapter(List<StorePlace> storePlaces) {
            mPlaces = storePlaces;
        }

        @Override
        public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_place, parent, false);
            return new PlaceHolder(view);
        }

        @Override
        public void onBindViewHolder(PlaceHolder holder, int position) {
            StorePlace storePlace = mPlaces.get(position);
            holder.bindPlace(storePlace);
        }

        @Override
        public int getItemCount() {
            return mPlaces.size();
        }

        public void setPlaces(List<StorePlace> storePlaces) {
            mPlaces = storePlaces;
        }
    }

    private static void startWithTransition(Activity activity, Intent intent,
                                            View sourceView, String transitionName) {

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, sourceView, transitionName);

        activity.startActivity(intent, options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            Place place = PlacePicker.getPlace(getActivity(), data);
            String id = place.getId();
            CharSequence name = place.getName();
            CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            StorePlace storePlace = new StorePlace(id);
            storePlace.setName(name);
            storePlace.setAddress(address);
            //storePlace.setAttributions(attributions);

            PlaceData.get(getActivity()).addPlace(storePlace);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
