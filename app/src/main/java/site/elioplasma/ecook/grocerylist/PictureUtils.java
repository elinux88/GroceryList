package site.elioplasma.ecook.grocerylist;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by eli on 4/12/16.
 */
public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create bitmap
        Bitmap src = BitmapFactory.decodeFile(path, options);
        if (src == null) {
            return null;
        }

        int width = src.getWidth();
        int height = src.getHeight();

        int startX = 0;
        int startY = 0;

        int squaredWidth = width;
        int squaredHeight = height;

        if (width > height) {
            squaredWidth = height;
            startX = (width - squaredWidth) / 2;
        }
        if (height > width) {
            squaredHeight = width;
            startY = (height - squaredHeight) / 2;
        }

        // Create a cropped square bitmap
        return Bitmap.createBitmap(src, startX, startY, squaredWidth, squaredHeight);
    }
}
