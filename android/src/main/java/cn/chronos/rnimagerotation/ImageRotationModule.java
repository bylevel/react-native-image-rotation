package cn.chronos.rnimagerotation;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by chronos on 01/03/16.
 */
public class ImageRotationModule extends ReactContextBaseJavaModule {
    private Context context;

    public ImageRotationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    @Override
    public String getName() {
        return "ImageRotationAndroid";
    }

    @ReactMethod
    public void createRotationImage(ReadableMap image, final Callback successCb, final Callback failureCb) {
        String imageUri = image.getString("uri");
        float degrees = (float) image.getInt("degrees"); // 指定角度旋转
        boolean auto = image.getBoolean("auto"); // 是否自动旋转
        String imagePath = getPath(imageUri);

        if (imagePath.equals("")) {
            failureCb.invoke("uri error");
            return;
        }

        WritableMap response = Arguments.createMap();
        try {
            if (auto) {
                degrees = _getDegrees(imageUri);
            }

            if (degrees == 0) {
                response.merge(image);
                // 将uri改成file://形式
                response.putString("uri", "file://" + imagePath);
                successCb.invoke(response);
                return;
            }

            // 生成旋转后的图片
            Bitmap bm = rotationImage(imagePath, degrees);
            // 保存图片
            response = saveImage(bm, context.getCacheDir(),
                    Long.toString(new Date().getTime()), Bitmap.CompressFormat.JPEG);
            successCb.invoke(response);
        } catch (IOException e) {
            failureCb.invoke(e.getMessage());
        }
    }

    // 获取照片需要旋转的角度
    @ReactMethod
    public void getDegrees(String imageUri, final Callback successCb) {
        // 返回给react
        successCb.invoke(_getDegrees(imageUri));
    }

    public float _getDegrees(String imageUri) {
        String imagePath = getPath(imageUri);
        if (imagePath.equals("")) {
            return 0;
        }

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            // 没有exif直接返回0
            return 0;
        }

        // 读取图片中相机方向信息
        int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        // 计算旋转角度
        switch (ori) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    // 旋转图片
    private static Bitmap rotationImage(String imagePath, float degrees) throws IOException {
        try {
            Bitmap image = BitmapFactory.decodeFile(imagePath);
            if (image == null) {
                return null; // Can't load the image from the given path.
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
                    throw new IOException("rotation error");
                }
            }

            // 返回已经被转动过的图片
            return image;
        } catch (OutOfMemoryError ex) {
            throw new IOException("rotation error");
        }
    }

    // 写入文件
    private static WritableMap saveImage(Bitmap bitmap, File saveDirectory, String fileName,
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

        // 生成返回内容
        WritableMap response = Arguments.createMap();
        response.putString("uri", "file://" + newFile.getAbsolutePath());
        // 获取旋转后的宽高
        response.putInt("width", bitmap.getWidth());
        response.putInt("height", bitmap.getHeight());

        return response;
    }


    // 获取url对应的path
    private String getPath(String imageUri) {
        // 处理uri
        if (imageUri.startsWith("file:")) {
            return Uri.parse(imageUri).getPath();
        } else if (imageUri.startsWith("content:")) {
            Cursor cursor = context.getContentResolver().query(Uri.parse(imageUri), null, null, null, null);
            if (cursor == null) {
                return "";
            }

            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                Uri filePathUri = Uri.parse(cursor.getString(column_index));
                return filePathUri.getPath();
            }
        } else if (!this.isAbsolutePath(imageUri)) {
            return "";
        }
        return imageUri;
    }

    // 判断是否是有效的文件路径
    private boolean isAbsolutePath(String path) {
        return (new File(path)).exists();
    }
}
