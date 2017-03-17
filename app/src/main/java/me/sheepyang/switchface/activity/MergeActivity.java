package me.sheepyang.switchface.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sheepyang.switchface.R;

public class MergeActivity extends BaseActivity {
    public static final String INTENT_FRONT_URI = "intent_front_uri";
    public static final String INTENT_BACK_URI = "intent_back_uri";
    @BindView(R.id.rl_container)
    RelativeLayout mRlContainer;
    private Uri mFrontUri;
    private Uri mBackUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        ButterKnife.bind(this);
        mFrontUri = getIntent().getParcelableExtra(INTENT_FRONT_URI);
        mBackUri = getIntent().getParcelableExtra(INTENT_BACK_URI);
        initView();
    }

    private void initView() {

    }
}
