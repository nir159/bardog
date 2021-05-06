package com.nalamala.bardog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Calendar;
import java.util.Date;

public class CommonFunctions {

    // Database reference
    public static final String DATABASE_USERS_REF = "Users";
    public static final String DATABASE_DOGS_REF = "Dogs";
    public static final String STORAGE_DOGS_IMAGES = "users";

    // User firebase value
    public static final String USER_FULL_NAME = "name";
    public static final String USER_PHONE_NUMBER = "phone";
    public static final String USER_ADDRESS = "address";

    // Dog class values
    public static final String DOG_NAME = "dog_name";
    public static final String IS_IMMUNIZED = "is_immunized";
    public static final String DOG_TYPE = "type";
    public static final String OWNER_NAME = "owner_name";
    public static final String OWNER_PHONE = "owner_phone";
    public static final String PROFILE_IMAGE = "profile_img";
    public static final String COMMENTS = "comments";
    public static final String OWNER_ADDRESS = "owner_address";
    public static final String BIRTH_DATE = "birth_year";
    public static final String DOG_ID = "dogID";

    // Register License Activity
    public static final String LICENSE_PROCESS = "license";

    // See Dog Activity
    public static final String DOG_EXTRA = "extra_dog_preview";

    // Generates a barcode
    public static Bitmap getBarcode(Context mContext, String dogID) {

        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;

        try {

            // bitMatrix.get(x, y) returns true if the square at x,y is black
            bitMatrix = new QRCodeWriter().encode("https://bardog.page.link/inf/?info=uid~" + currentUserUid + "~" + dogID,
                    BarcodeFormat.QR_CODE, 350, 300);
            Bitmap barcode = Bitmap.createBitmap(bitMatrix.getWidth(), bitMatrix.getHeight(), Bitmap.Config.ARGB_8888);

            // drawing the rounded qr
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                for (int x = 0; x < bitMatrix.getWidth(); x++) {
                    if (bitMatrix.get(x, y))
                        canvas.drawCircle(x, y, 2f, paint);
                }
            }

            // add the logo
            // Bitmap logoImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_qr_black);
            Bitmap logoImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_qr_black);
            logoImage = CommonFunctions.ReduceBitmapSize(logoImage, 1500);

            Bitmap bmOverlay = Bitmap.createBitmap(barcode.getWidth(), barcode.getHeight(), barcode.getConfig());
            canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(barcode, new Matrix(), null);
            // paint.setColor(Color.WHITE);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(barcode.getWidth()/2, barcode.getHeight()/2, 25, paint);
            canvas.drawBitmap(logoImage, barcode.getWidth()/2 - logoImage.getWidth()/2, barcode.getHeight()/2 - logoImage.getHeight()/2, null);
            barcode.recycle();
            logoImage.recycle();

            return bmOverlay;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;

    }

    // Reduce image resolution
    public static Bitmap ReduceBitmapSize(Bitmap originalBitmap, int maxSize) {

        final int MAX_SIZE = maxSize;

        double ratio;
        int height = originalBitmap.getHeight();
        int width = originalBitmap.getWidth();
        ratio = (width * height) / MAX_SIZE;
        if (ratio <= 1)
            return originalBitmap;

        ratio = Math.sqrt(ratio);
        width = (int) Math.round(width / ratio);
        height = (int) Math.round(height / ratio);

        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }

}
