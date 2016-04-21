package site.elioplasma.ecook.grocerylist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by eli on 3/2/16.
 */
public class PlaceFragment extends Fragment {

    private static final String TAG = "GroceryList";
    private static final String ARG_PLACE_ID = "place_id";

    private static final int REQUEST_PHOTO = 0;

    private StorePlace mPlace;
    private File mPhotoFile;
    private TextView mPlaceName;
    private TextView mPlaceAddress;
    private TextView mPlaceAttributions;
    private ImageView mPlacePicture;
    final Intent mCaptureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    public static PlaceFragment newInstance(String placeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE_ID, placeId);

        PlaceFragment fragment = new PlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        String placeId = getArguments().getString(ARG_PLACE_ID);

        mPlace = PlaceData.get(getActivity()).getPlace(placeId);
        mPhotoFile = PlaceData.get(getActivity()).getPhotoFile(mPlace);
    }

    @Override
    public void onPause() {
        super.onPause();

        PlaceData.get(getActivity())
                .updatePlace(mPlace);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_place, container, false);
        mPlaceName = (TextView) v.findViewById(R.id.place_name_view);
        mPlaceName.setText(mPlace.getName());

        mPlaceAddress = (TextView) v.findViewById(R.id.place_address_view);
        mPlaceAddress.setText(mPlace.getAddress());
        mPlaceAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + mPlace.getAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        mPlaceAttributions = (TextView) v.findViewById(R.id.place_attributions_view);
        mPlaceAttributions.setText(Html.fromHtml(mPlace.getAttributions()));

        mPlacePicture = (ImageView) v.findViewById(R.id.place_picture_view);
        updatePhotoView();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_place, menu);

        MenuItem photoItem = menu.findItem(R.id.menu_item_take_photo);
        PackageManager packageManager = getActivity().getPackageManager();

        boolean canTakePhoto = mPhotoFile != null && mCaptureImage.resolveActivity(packageManager) != null;
        photoItem.setVisible(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            mCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().finishAfterTransition();
                }
                return true;
            case R.id.menu_item_take_photo:
                mPlace.setPhotoType(Item.PHOTO_CAMERA);
                startActivityForResult(mCaptureImage, REQUEST_PHOTO);
                return true;
            case R.id.menu_item_fetch_photo:
                mPlace.setPhotoType(Item.PHOTO_INTERNET);
                new SearchTask().execute(mPlace.getName());
                return true;
            case R.id.menu_item_delete_place:
                String id = mPlace.getId();
                if (PlaceData.get(getActivity()).deletePlace(id)) {
                    getActivity().finish();
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPlacePicture.setImageResource(R.drawable.grocery_bag);
            mPlace.setPhotoType(Item.PHOTO_DEFAULT);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            if (bitmap != null) {
                mPlacePicture.setImageBitmap(bitmap);
            } else {
                mPlacePicture.setImageResource(R.drawable.grocery_bag);
                mPlace.setPhotoType(Item.PHOTO_DEFAULT);
            }
        }
    }

    private class SearchTask extends AsyncTask<String, Void, Void> {
        private Bitmap mBitmap;

        @Override
        protected Void doInBackground(String... params) {
            FlickrFetchr fetchr = new FlickrFetchr();
            List<GalleryItem> items = fetchr.searchPhotos(params[0]);

            if (items.size() == 0) {
                return null;
            }

            GalleryItem galleryItem = items.get(0);
            galleryItem.setUrl(fetchr.getPhotoUrl(galleryItem.getId()));

            try {
                byte[] bytes = fetchr.getUrlBytes(galleryItem.getUrl());
                mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (IOException ioe) {
                Log.i(TAG, "Unable to download bitmap", ioe);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String path = getActivity().getFilesDir().toString();
            String fileName = mPlace.getPhotoFilename();
            File file = new File(path, fileName);

            OutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);
            } catch (FileNotFoundException fnfe) {
                Log.i(TAG, "Unable to create file output stream", fnfe);
            }

            if (mBitmap != null) {
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            }

            try {
                fOut.flush();
                fOut.close();
            } catch (IOException ioe) {
                Log.i(TAG, "Unable to close file output stream", ioe);
            }

            mPhotoFile = file;
            updatePhotoView();
        }
    }
}
