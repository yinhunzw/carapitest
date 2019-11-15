package com.gwm.carapitest.tool;

import android.app.ActivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gwm.carapitest.R;

public class TestActivity1 extends TestActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
