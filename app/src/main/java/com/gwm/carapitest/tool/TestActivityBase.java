package com.gwm.carapitest.tool;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.gwm.carapitest.Interfaces.IActivityStateListener;
import com.gwm.carapitest.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TestActivityBase extends AppCompatActivity {
    private final static int ON_CREATE = 1 << 0;
    private final static int ON_START = 1 << 1;
    private final static int ON_RESTART = 1 << 2;
    private final static int ON_NEW_INTENT = 1 << 3;
    private final static int ON_RESUME= 1 << 4;
    private final static int ON_PAUSE = 1 << 5;
    private final static int ON_STOP = 1 << 6;
    private final static int ON_DESTROY= 1 << 7;
    private List<IActivityStateListener> mActivityStateListener = new ArrayList<IActivityStateListener>();
    private static TestActivityBase mInstance;
    private int mCurrentState = 0;

    public static TestActivityBase getInstance() {
        return mInstance;
    }
    public void registerActivityStateListener(IActivityStateListener activityStateListener) {
        mActivityStateListener.add(activityStateListener);
        if (mCurrentState == ON_RESUME) {
            mActivityStateListener.forEach(listener->listener.onResume());
        }
    }
    public void UnregisterActivityStateListener(IActivityStateListener activityStateListener) {
        mActivityStateListener.remove(activityStateListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mInstance = this;
        mCurrentState = ON_CREATE;
        mActivityStateListener.forEach(listener->listener.onCreate());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentState = ON_START;
        mActivityStateListener.forEach(listener->listener.onStart());
        ((TextView)findViewById(R.id.id_function)).setText(getClass().getSimpleName());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCurrentState = ON_RESTART;
        mActivityStateListener.forEach(listener->listener.onRestart());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mCurrentState = ON_NEW_INTENT;
        mActivityStateListener.forEach(listener->listener.onNewIntent());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mActivityStateListener.forEach(listener->listener.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mActivityStateListener.forEach(listener->listener.onRestoreInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentState = ON_RESUME;
        mActivityStateListener.forEach(listener->listener.onResume());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentState = ON_PAUSE;
        mActivityStateListener.forEach(listener->listener.onPause());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCurrentState = ON_STOP;
        mActivityStateListener.forEach(listener->listener.onStop());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurrentState = ON_DESTROY;
        mActivityStateListener.forEach(listener->listener.onDestroy());
    }
}
