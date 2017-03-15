package me.sheepyang.switchface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sheepyang.switchface.enhance.PhotoEnhance;

/**
 * 编辑前景图片
 */
public class EditFrontActivity extends BaseActivity implements View.OnTouchListener {

    public static final String INTENT_URI = "intent_uri";
    private static final int POST_SATURATION = 0x01;
    private static final int POST_BRIGHTNESS = 0x02;
    private static final int POST_CONTRAST = 0x03;

    @BindView(R.id.iv_front)
    ImageView mIvFront;
    @BindView(R.id.wheel_saturation)
    HorizontalProgressWheelView mWheelSaturation;
    @BindView(R.id.wheel_brightness)
    HorizontalProgressWheelView mWheelBrightness;
    @BindView(R.id.wheel_contrast)
    HorizontalProgressWheelView mWheelContrast;
    @BindView(R.id.tv_saturation)
    TextView mTvSaturation;
    @BindView(R.id.tv_brightness)
    TextView mTvBrightness;
    @BindView(R.id.tv_contrast)
    TextView mTvContrast;
    private Paint mEraserPaint;
    private Bitmap mBitmapSrc;
    private Uri mFrontUri;
    private PhotoEnhance pe;
    private Matrix mImageMatrix = new Matrix();
    private long mTempTime;
    private float mSaturation = 128;//图片饱和度0~255
    private float mBrightness = 128;//图片明亮度0~255
    private float mContrast = 128;//图片对比度0~255

    private float downX = 0;
    private float downY = 0;
    private float upX = 0;
    private float upY = 0;
    private Canvas mCanvas;
    private Bitmap bmp;
    private Paint mPaint;
    private Bitmap mCopyPreBitmap;
    private Path mPath;
    int startX;//声明startX变量
    int startY;//声明startY变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_front);
        ButterKnife.bind(this);
        mFrontUri = getIntent().getParcelableExtra(INTENT_URI);
        initView();
        initListener();
    }

    private void initListener() {
        mIvFront.setOnTouchListener(this);
        mWheelSaturation.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(float delta, float totalDistance) {
                Message msg = Message.obtain();
                msg.what = POST_SATURATION;
                msg.obj = delta;
                mTempTime = System.currentTimeMillis();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onScrollEnd() {

            }
        });
        mWheelBrightness.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(float delta, float totalDistance) {
                Message msg = Message.obtain();
                msg.what = POST_BRIGHTNESS;
                msg.obj = delta;
                mTempTime = System.currentTimeMillis();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onScrollEnd() {

            }
        });
        mWheelContrast.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(float delta, float totalDistance) {
                Message msg = Message.obtain();
                msg.what = POST_CONTRAST;
                msg.obj = delta;
                mTempTime = System.currentTimeMillis();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onScrollEnd() {

            }
        });
    }

    private void initView() {
        mBitmapSrc = loadBitmap(mFrontUri);
        mCopyPreBitmap = Bitmap.createBitmap(mBitmapSrc.getWidth(), mBitmapSrc.getHeight(), mBitmapSrc.getConfig());
        mCanvas = new Canvas(mCopyPreBitmap);
        mPath = new Path();//实例化画图类

        mPaint = new Paint();        //抗锯齿
        mPaint.setColor(Color.RED);//设置画笔颜色
        mPaint.setStrokeWidth(100);//设置描边宽度
//        BlurMaskFilter bmf = new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL);//指定了一个模糊的样式和半径来处理Paint的边缘。
//        mPaint.setMaskFilter(bmf);//为Paint分配边缘效果。
        mPaint.setStyle(Paint.Style.STROKE);//让画出的图形是空心的
        mPaint.setStrokeJoin(Paint.Join.ROUND);//设置结合处的样子 Miter:结合处为锐角， Round:结合处为圆弧：BEVEL：结合处为直线。
        mPaint.setStrokeCap(Paint.Cap.ROUND);//画笔笔刷类型   方形形状
        mCanvas.drawBitmap(mBitmapSrc, new Matrix(), mPaint);
        mIvFront.setImageBitmap(mCopyPreBitmap);
        pe = new PhotoEnhance(mBitmapSrc);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));//它的作用是用此画笔后，画笔划过的痕迹就变成透明色了。画笔设置好了后，就可以调用该画笔进行橡皮痕迹的绘制了
    }

    private Bitmap loadBitmap(Uri uri) {
        Bitmap bitmap = null;

        InputStream input;
        try {
            input = getContentResolver().openInputStream(uri);
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();
            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;
            if ((originalWidth == -1) || (originalHeight == -1))
                return null;
            //图片分辨率以480x800为标准
            float hh = 800f;//这里设置高度为800f
            float ww = 800f;//这里设置宽度为800f
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;//be=1表示不缩放
//            if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
//                be = (int) (originalWidth / ww);
//            } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
//                be = (int) (originalHeight / hh);
//            }
//            if (be <= 0) {
//                be = 1;
//            }
            //比例压缩
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = be;//设置缩放比例
            bitmapOptions.inDither = true;//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            input = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            Matrix matrix = new Matrix();
            matrix.postScale(ww / originalWidth, ww / originalWidth);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            float deltaDistance = (float) (1 / (float) 2 * (float) msg.obj / 16 * Math.sqrt(System.currentTimeMillis() - mTempTime));
            switch (msg.what) {
                case POST_SATURATION:
                    mSaturation += deltaDistance;
                    if (mSaturation <= 0) {
                        mSaturation = 0;
                    } else if (mSaturation >= 255) {
                        mSaturation = 255;
                    }
                    mTvSaturation.setText((int) (mSaturation / 255 * 100) + "%");
                    pe.setSaturation((int) mSaturation);//0~255, 默认128
                    mCopyPreBitmap = pe.handleImage(pe.Enhance_Saturation);
                    break;
                case POST_BRIGHTNESS:
                    mBrightness += deltaDistance;
                    if (mBrightness <= 0) {
                        mBrightness = 0;
                    } else if (mBrightness >= 255) {
                        mBrightness = 255;
                    }
                    mTvBrightness.setText((int) (mBrightness / 255 * 100) + "%");
                    pe.setBrightness((int) mBrightness);//0~255, 默认128
                    mCopyPreBitmap = pe.handleImage(pe.Enhance_Brightness);
                    break;
                case POST_CONTRAST:
                    mContrast += deltaDistance;
                    if (mContrast <= 0) {
                        mContrast = 0;
                    } else if (mContrast >= 255) {
                        mContrast = 255;
                    }
                    mTvContrast.setText((int) (mContrast / 255 * 100) + "%");
                    pe.setContrast((int) mContrast);//0~255, 默认128
                    mCopyPreBitmap = pe.handleImage(pe.Enhance_Contrast);
                    break;
                default:
                    break;
            }
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mPaint.setXfermode(null);
            mCanvas.drawBitmap(mCopyPreBitmap, new Matrix(), mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));//它的作用是用此画笔后，画笔划过的痕迹就变成透明色了。画笔设置好了后，就可以调用该画笔进行橡皮痕迹的绘制了
            mCanvas.drawPath(mPath, mPaint);//绘制图像
            mIvFront.invalidate();
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();//获得触摸的X轴位置
        int y = (int) event.getY();//获得触摸的Y轴位置
        int position = event.getAction();//获得的返回值 获取触控动作比如ACTION_DOWN

        int endX = 0;//声明变量endX
        int endY = 0;//声明变量endY

        switch (position) {
            case MotionEvent.ACTION_DOWN://当触摸时按下时
                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_MOVE://当触摸移动时
                endX = x;
                endY = y;
                mPath.moveTo(startX, startY);//起始点
                mPath.lineTo(endX, endY);//终点
                mCanvas.drawPath(mPath, mPaint);//绘制图像
                mIvFront.postInvalidate();//刷新界面

                startX = endX;//将 endX值 也就是停止触摸时X轴的位置 付给 startX当中
                startY = endY;//将 endY值 也就是停止触摸时X轴的位置 付给 startY当中
                break;
            case MotionEvent.ACTION_UP://当触摸结束时
                break;
        }
        return true;
    }
}
