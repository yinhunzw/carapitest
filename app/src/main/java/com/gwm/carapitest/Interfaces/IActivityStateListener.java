package com.gwm.carapitest.Interfaces;

public interface IActivityStateListener {
    void onCreate();
    void onStart();
    void onRestart();
    void onNewIntent();
    void onResume();
    void onPause();
    void onSaveInstanceState();
    void onRestoreInstanceState();
    void onStop();
    void onDestroy();
}
