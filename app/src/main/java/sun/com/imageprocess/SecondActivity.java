package sun.com.imageprocess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener{

    private TouchImageView mTouchImageView;
    private LinearLayout mLinearLayout;
    private Bitmap srcBitmap;

    private Button buttonInit;
    private Button buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        srcBitmap = BitmapUtils.getBitmapFromFile("srcBitmap"); //从存储中读取保存bitmap
        if(srcBitmap == null){
            initEmptySrcBitmap();
        }

        mTouchImageView = new TouchImageView(srcBitmap,SecondActivity.this);

        mLinearLayout = (LinearLayout) findViewById(R.id.touchImageLayout);
        mLinearLayout.addView(mTouchImageView);

        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonInit = (Button) findViewById(R.id.buttonInit1);
        buttonInit.setOnClickListener(this);
        buttonExit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonInit1:
                mLinearLayout.removeAllViews();
                initEmptySrcBitmap();
                mTouchImageView = new TouchImageView(srcBitmap,SecondActivity.this);
                mLinearLayout.addView(mTouchImageView);
                break;
            case R.id.buttonExit:
                finish();
                break;
            default:
                break;
        }
    }

    private void initEmptySrcBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 10;
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image1,options);
    }
}
