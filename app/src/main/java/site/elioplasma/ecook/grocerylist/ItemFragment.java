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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by eli on 3/2/16.
 */
public class ItemFragment extends Fragment {

    private static final String TAG = "GroceryList";
    private static final String ARG_ITEM_ID = "item_id";

    private static final int REQUEST_PHOTO = 0;

    private Item mItem;
    private File mPhotoFile;
    private EditText mItemName;
    private EditText mItemAmount;
    private ImageView mItemPicture;
    final Intent mCaptureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    public static ItemFragment newInstance(UUID itemId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_ID, itemId);

        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID itemId = (UUID) getArguments().getSerializable(ARG_ITEM_ID);

        mItem = ItemData.get(getActivity()).getItem(itemId);
        mPhotoFile = ItemData.get(getActivity()).getPhotoFile(mItem);
    }

    @Override
    public void onPause() {
        super.onPause();

        ItemData.get(getActivity())
                .updateItem(mItem);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item, container, false);
        mItemName = (EditText) v.findViewById(R.id.item_name_view);
        mItemName.setText(mItem.getName());
        mItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mItem.setName(mItemName.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // not used
            }
        });

        mItemAmount = (EditText) v.findViewById(R.id.item_amount_text);
        mItemAmount.setText(Integer.toString(mItem.getAmount()));
        mItemAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int amount;
                try {
                    amount = Integer.parseInt(mItemAmount.getText().toString());
                } catch (NumberFormatException nfe) {
                    Toast.makeText(getActivity(), "Please enter a whole number", Toast.LENGTH_SHORT).show();
                    return;
                }
                mItem.setAmount(amount);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // not used
            }
        });

        mItemPicture = (ImageView) v.findViewById(R.id.item_picture_view);
        updatePhotoView();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_item, menu);

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
                mItem.setPhotoType(Item.PHOTO_CAMERA);
                startActivityForResult(mCaptureImage, REQUEST_PHOTO);
                return true;
            case R.id.menu_item_fetch_photo:
                mItem.setPhotoType(Item.PHOTO_INTERNET);
                new SearchTask().execute("\"grocery " + mItem.getName() + "\"");
                return true;
            case R.id.menu_item_delete_item:
                UUID id = mItem.getId();
                if (ItemData.get(getActivity()).deleteItem(id)) {
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
            mItemPicture.setImageResource(R.drawable.grocery_bag);
            mItem.setPhotoType(Item.PHOTO_DEFAULT);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            if (bitmap != null) {
                mItemPicture.setImageBitmap(bitmap);
            } else {
                mItemPicture.setImageResource(R.drawable.grocery_bag);
                mItem.setPhotoType(Item.PHOTO_DEFAULT);
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
            String fileName = mItem.getPhotoFilename();
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
