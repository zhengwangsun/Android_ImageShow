package sun.com.imageprocess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,View.OnClickListener{

    private SeekBar seekBarSaturation;          //饱和度
    private SeekBar seekBarBrightness;          //亮度
    private SeekBar seekBarContrast;            //对比度

    private static final int MAX_VALUE = 255;   //seekBar的进度条最大值
    private static final int MID_VALUE = 127;

    private ImageView mImageView;
    private Bitmap srcBitmap;

    private Button button1;
    private Button button2;
    private Button buttonChoose;
    private Button buttonInit;

    private boolean COVERT_MODE;

    private static final int PHOTO_REQUEST_CAREMA = 1;   // 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;  // 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;      // 结果

    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        seekBarSaturation = (SeekBar) findViewById(R.id.seekBarSaturation);
        seekBarBrightness = (SeekBar) findViewById(R.id.seekBarBrightness);
        seekBarContrast = (SeekBar) findViewById(R.id.seekBarContrast);
        seekBarSaturation.setMax(MAX_VALUE);
        seekBarBrightness.setMax(MAX_VALUE);
        seekBarContrast.setMax(MAX_VALUE);
        seekBarSaturation.setProgress(MID_VALUE);
        seekBarBrightness.setProgress(MID_VALUE);
        seekBarContrast.setProgress(MID_VALUE);

        seekBarSaturation.setOnSeekBarChangeListener(this);
        seekBarBrightness.setOnSeekBarChangeListener(this);
        seekBarContrast.setOnSeekBarChangeListener(this);

        mImageView = (ImageView) findViewById(R.id.imageView);


        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        buttonChoose =(Button) findViewById(R.id.buttonChoose);
        buttonInit =(Button) findViewById(R.id.buttonInit);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        buttonChoose.setOnClickListener(this);
        buttonInit.setOnClickListener(this);

        initSrcBitmap();
        COVERT_MODE = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                if(!COVERT_MODE){
                    COVERT_MODE = true;
                    button1.setText("显示原图");
                    mImageView.setImageBitmap(ImageHelper.convertBitmap(srcBitmap));
                }else{
                    COVERT_MODE = false;
                    button1.setText("显示镜像");
                    mImageView.setImageBitmap(srcBitmap);
                }
                break;
            case R.id.button2:
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                BitmapUtils.saveBitmap2file(srcBitmap,"srcBitmap");  //将bitmap保存至本地存储
                startActivity(intent);
                break;
            case R.id.buttonChoose:
                gallery(mImageView);
                break;
            case R.id.buttonInit:
                initSrcBitmap();
                break;
            default:
                break;
        }
    }

    public void initSrcBitmap(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 10;
        srcBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.image1,options);
        mImageView.setImageBitmap(srcBitmap);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.seekBarSaturation:
                float afterProgress = progress * 1.0F / MID_VALUE;
                mImageView.setImageBitmap(ImageHelper.handleBitmapSaturation(srcBitmap,afterProgress));
                break;
            case R.id.seekBarBrightness:
                int brightness = progress - MID_VALUE;
                mImageView.setImageBitmap(ImageHelper.handleBitmapBrightness(srcBitmap,brightness));
                break;
            case R.id.seekBarContrast:
                float contrast = (progress + MID_VALUE) * 1.0F / MAX_VALUE;
                mImageView.setImageBitmap(ImageHelper.handleBitmapContrast(srcBitmap,contrast));
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    /**
     * 从相册获取
     */
    public void gallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    PHOTO_FILE_NAME);
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
     * 剪切图片
     */
    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /*
     * 判断sdcard是否被挂载
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(MainActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                this.srcBitmap = bitmap;
                this.mImageView.setImageBitmap(bitmap);
            }
            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
