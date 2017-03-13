package me.sheepyang.switchface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sheepyang.switchface.enhance.PhotoEnhance;
import me.sheepyang.switchface.utils.StorageUtil;

/**
 * 编辑前景图片
 */
public class EditFrontActivity extends BaseActivity {

    public static final String INTENT_URI = "intent_uri";
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
    @BindView(R.id.tv_total_distance)
    TextView mTvTotalDistance;
    private Bitmap mBitmapSrc;
    private Bitmap tempBitmap = null;
    private Uri mFrontUri;
    private PhotoEnhance pe;

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
        mWheelSaturation.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(float delta, float totalDistance) {
                mTvTotalDistance.setText(totalDistance + "");
                float sauration = delta * pe.getSaturation();
                mTvSaturation.setText(sauration + "");
//                pe.setSaturation(10);
//                //0~255, 默认128
//                int type = pe.Enhance_Saturation;
//                tempBitmap = pe.handleImage(type);
//                mIvFront.setImageBitmap(tempBitmap);
            }

            @Override
            public void onScrollEnd() {

            }
        });
    }

    private void initView() {
        String path = StorageUtil.getPath(mContext, mFrontUri);
        mBitmapSrc = BitmapFactory.decodeFile(path);
        Glide.with(this)
                .load(mBitmapSrc)
                .centerCrop()
                .placeholder(R.drawable.select_image)
                .into(mIvFront);
        pe = new PhotoEnhance(mBitmapSrc);
    }
}
