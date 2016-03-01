package cn.chronos.rnimagerotation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by chronos on 01/03/16.
 */
class ImageRotation {

    private static Bitmap rotationImage(String imagePath) throws IOException {
        try {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e) {
                exif = null;
                // 返回没有exif的错误提示,表示普通的jpeg文件直接跳过
                throw new IOException("hasn't exif");
            }

            Bitmap image = BitmapFactory.decodeFile(imagePath);
            if (image == null) {
                return null; // Can't load the image from the given path.
            }

            float degrees = 0;

            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
                    break;
                default:
                    degrees = 0;
                    break;
            }

            if (degrees != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(degrees);
                try {
                    Bitmap tmpImg = Bitmap.createBitmap(image, 0, 0, image.getWidth(),
                            image.getHeight(), m, true);

                    if (image != tmpImg) {
                        image.recycle();
                        image = tmpImg;
                    }

                } catch (OutOfMemoryError ex) {
                    //
                    return null;
                }
            } else {
                // 如果没有旋转角度则直接返回错误,不再转动
                throw new IOException("No need rotation");
            }

            // 返回已经被转动过的图片
            return image;
        } catch (OutOfMemoryError ex) {
            // No memory available for resizing.
        }

        return null;
    }

    private static String saveImage(Bitmap bitmap, File saveDirectory, String fileName,
                                    Bitmap.CompressFormat compressFormat)
            throws IOException {
        if (bitmap == null) {
            throw new IOException("The bitmap couldn't be rotation");
        }

        File newFile = new File(saveDirectory, fileName + "." + compressFormat.name());
        if (!newFile.createNewFile()) {
            throw new IOException("The file already exists");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, 95, outputStream);
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
        Bitmap rotateImage;
        try {
            rotateImage = ImageRotation.rotationImage(imagePath);
        } catch (IOException e) {
            // 如果出错表示不需要旋转,直接返回原文件
            return imagePath;
        }

        return ImageRotation.saveImage(rotateImage, context.getCacheDir(),
                Long.toString(new Date().getTime()), compressFormat);
    }
}
