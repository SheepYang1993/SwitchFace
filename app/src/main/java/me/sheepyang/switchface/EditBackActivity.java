package me.sheepyang.switchface;

import android.os.Bundle;

public class EditBackActivity extends BaseActivity {
    public static final String INTENT_URI = "intent_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_back);
    }
}
