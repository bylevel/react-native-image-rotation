package cn.chronos.rnimagerotation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by chronos on 01/03/16.
 */
class ImageRotation {

    private static Bitmap rotationImage(String imagePath, int maxWidth, int maxHeight) {
        try {
            Bitmap image = BitmapFactory.decodeFile(imagePath);
            if (image == null) {
                return null; // Can't load the image from the given path.
            }

            if (maxHeight > 0 && maxWidth > 0) {
                int width = image.getWidth();
                int height = image.getHeight();
                float ratioBitmap = (float) width / (float) height;
                float ratioMax = (float) maxWidth / (float) maxHeight;

                int finalWidth = maxWidth;
                int finalHeight = maxHeight;
                if (ratioMax > 1) {
                    finalWidth = (int) ((float) maxHeight * ratioBitmap);
                } else {
                    finalHeight = (int) ((float) maxWidth / ratioBitmap);
                }
                image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            }

            return image;
        } catch (OutOfMemoryError ex) {
            // No memory available for resizing.
        }

        return null;
    }

    public static Bitmap rotateImage(Bitmap b, float degrees)
    {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees,
                    (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // No memory available for rotating. Return the original bitmap.
            }
        }
        return b;
    }

    private static String saveImage(Bitmap bitmap, File saveDirectory, String fileName,
                                    Bitmap.CompressFormat compressFormat)
            throws IOException {
        if (bitmap == null) {
            throw new IOException("The bitmap couldn't be resized");
        }

        File newFile = new File(saveDirectory, fileName + "." + compressFormat.name());
        if(!newFile.createNewFile()) {
            throw new IOException("The file already exists");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, 100, outputStream);
        byte[] bitmapData = outputStream.toByteArray();

        outputStream.flush();
        outputStream.close();

        FileOutputStream fos = new FileOutputStream(newFile);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        return newFile.getAbsolutePath();
    }

    // 新建自动旋转的图片
    public static String createAutoRotationImage(Context context, String imagePath, Bitmap.CompressFormat compressFormat) throws IOException {

        Bitmap rotateImage = ImageRotation.rotateImage(ImageRotation.rotationImage(imagePath));
        return ImageRotation.saveImage(rotateImage, context.getCacheDir(),
                Long.toString(new Date().getTime()), compressFormat);
    }
}
