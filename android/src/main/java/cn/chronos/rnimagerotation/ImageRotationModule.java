package cn.chronos.rnimagerotation;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.IOException;

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
    public void createRotationImage(String imagePath, final Callback successCb, final Callback failureCb) {
        try {
            createRotationImageWithExceptions(imagePath, successCb, failureCb);
        } catch (IOException e) {
            failureCb.invoke(e.getMessage());
        }
    }

    private void createRotationImageWithExceptions(String imagePath,
                                                   final Callback successCb, final Callback failureCb) throws IOException {
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        // 处理uri
        if (imagePath.startsWith("file:")) {
            imagePath = Uri.parse(imagePath).getPath();
        } else if (imagePath.startsWith("content:")) {
            Cursor cursor = context.getContentResolver().query(Uri.parse(imagePath),null,null,null,null);
            if(cursor == null){
                throw new IOException("get path err");
            }
            if (cursor.moveToFirst()){
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                Uri filePathUri = Uri.parse(cursor.getString(column_index));
                imagePath = filePathUri.getPath();
            }
        } else if (!this.isAbsolutePath(imagePath)) {
            throw new IOException("upload error" + "Can't handle " + imagePath);
        }

        WritableMap response = ImageRotation.createAutoRotationImage(this.context, imagePath, compressFormat);

        successCb.invoke(response);
    }

    // 判断是否是有效的文件路径
    private boolean isAbsolutePath(String path) {
        return (new File(path)).exists();
    }
}
