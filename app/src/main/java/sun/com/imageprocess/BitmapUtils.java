package sun.com.imageprocess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * Created by sun on 2016/12/5.
 */

public class BitmapUtils {

    /**
     * 保存Bitmap为文件;可能报空指针是因为没有配置权限
     *
     * @param bmp
     * @param filename
     * @return
     */
    public static void saveBitmap2file(Bitmap bmp, String filename)
    {
        CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try
        {
            stream = new FileOutputStream(Environment
                    .getExternalStorageDirectory().getPath()
                    + "/temp/"
                    + filename
                    + ".jpg");
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        if(stream != null){
            bmp.compress(format, quality, stream);
            try
            {
                stream.flush();
                stream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件为Bitmap
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap getBitmapFromFile(String filename)

    {
        try
        {
            FileInputStream inputStream = new FileInputStream(Environment
                    .getExternalStorageDirectory().getPath()
                    + "/temp/"
                    + filename
                    + ".jpg");
            if(inputStream != null){
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
