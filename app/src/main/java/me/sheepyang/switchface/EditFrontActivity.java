package me.sheepyang.switchface;

import android.os.Bundle;

/**
 * 编辑前景图片
 */
public class EditFrontActivity extends BaseActivity {

    public static final String INTENT_URI = "intent_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_front);
    }
}
