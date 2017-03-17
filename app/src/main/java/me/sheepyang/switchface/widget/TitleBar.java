package me.sheepyang.switchface.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.sheepyang.switchface.R;

/**
 * Created by SheepYang on 2017/3/17.
 */

public class TitleBar extends RelativeLayout implements View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_right)
    TextView mTvRight;
    @BindView(R.id.iv_left)
    ImageView mIvLeft;
    private String mTitleText;
    private float mTitleTextSize;
    private int mTitleColor;
    private String mLeftBtnText;
    private float mLeftBtnTextSize;
    private String mRightBtnText;
    private float mRghtBtnTextSize;
    private Context mContext;
    private boolean mBackPressed = true;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.title_bar, this);
        ButterKnife.bind(this, view);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.title_bar);
        mTitleText = ta.getString(R.styleable.title_bar_title_text);
        mTitleTextSize = ta.getDimension(R.styleable.title_bar_title_text_size, 10);
        mTitleColor = ta.getColor(R.styleable.title_bar_title_color, 0);
        mLeftBtnText = ta.getString(R.styleable.title_bar_left_btn_text);
        mLeftBtnTextSize = ta.getDimension(R.styleable.title_bar_left_btn_text_size, 10);
        mRightBtnText = ta.getString(R.styleable.title_bar_right_btn_text);
        mRghtBtnTextSize = ta.getDimension(R.styleable.title_bar_right_btn_text_size, 10);
        ta.recycle();

        mTvTitle.setText(mTitleText);
        mTvRight.setText(mRightBtnText);
        if (mTvRight.getVisibility() != VISIBLE) {
            mTvRight.setVisibility(VISIBLE);
        }
        if (mBackPressed) {
            mIvLeft.setVisibility(mBackPressed ? VISIBLE : GONE);
        }
    }

    public void setTitle(String title) {
        mTitleText = title;
        mTvTitle.setText(mTitleText);
    }

    public void setRightBtnText(String rightBtnText, OnClickListener listener) {
        mRightBtnText = rightBtnText;
        mTvRight.setText(mRightBtnText);
        mTvRight.setOnClickListener(listener);
        if (mTvRight.getVisibility() != VISIBLE) {
            mTvRight.setVisibility(VISIBLE);
        }
    }

    public void showBackPressed(boolean backPressed) {
        mBackPressed = backPressed;
        mIvLeft.setVisibility(mBackPressed ? VISIBLE : GONE);
    }

    @Override
    @OnClick({R.id.iv_left, R.id.tv_right})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                if (mContext instanceof Activity && mBackPressed) {
                    ((Activity) mContext).onBackPressed();
                }
                break;
            case R.id.tv_right:
                break;
            default:
                break;
        }
    }
}
