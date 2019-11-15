package com.gwm.carapitest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gwm.carapitest.APITest.CarTestResult;
import com.gwm.carapitest.APITest.ClassCarAppFocusManagerTest;
import com.gwm.carapitest.APITest.ClassCarCabinManagerTest;
import com.gwm.carapitest.APITest.ClassCarHvacManagerTest;
import com.gwm.carapitest.APITest.ClassCarInfoManagerTest;
import com.gwm.carapitest.APITest.ClassCarPowerManagerTest;
import com.gwm.carapitest.APITest.ClassCarProjectionManagerTest;
import com.gwm.carapitest.APITest.ClassCarPropertyConfigTest;
import com.gwm.carapitest.APITest.ClassCarSensorManagerTest;
import com.gwm.carapitest.APITest.ClassAppBlockingPackageInfoTest;
import com.gwm.carapitest.APITest.ClassCarAppBlockingPolicyTest;
import com.gwm.carapitest.APITest.ClassCarTest;
import com.gwm.carapitest.APITest.ClassCarVendorExtensionManagerTest;
import com.gwm.carapitest.Interfaces.IListenTest;
import com.gwm.carapitest.utils.ExcelUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IListenTest, View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int MAX_INDEX = 40;
    private ListView mListView;
    private int mIndex;
    private ResultListAdapter mAdapter;
    private List<CarTestResult> mResultList = new ArrayList<CarTestResult>();
    private List<CarTestResult> mFilterResultList = new ArrayList<CarTestResult>();
    private final MainHandler mHandler = new MainHandler(this);

    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnExport;
    private EditText mEtInput;
    private Button mBtnFilter;
    private TextView mTestStatus;
    private Button mBtnShowAll;
    private ExcelUtil mExcelUtil;
    private boolean mIsSearchMode = false;

    private static final int MSG_START_TEST = 0;
    private static final int MSG_REFRESH_LIST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mAdapter = new ResultListAdapter(this, mResultList);
        mListView.setAdapter(mAdapter);
        mExcelUtil = new ExcelUtil(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_test:
                mIsSearchMode = false;
                mIndex = 0;
                mResultList.clear();
                mTestStatus.setText(R.string.testing);
                startTest();
                break;
            case R.id.btn_stop_test:
                mIndex = MAX_INDEX;
                mTestStatus.setText(R.string.test_stop);
                break;
            case R.id.btn_export:
                mExcelUtil.exportExcel("result" + mExcelUtil.getCurrentTimeString() + ".xls", mResultList);
                break;
            case R.id.btn_showall:
                mIsSearchMode = false;
                mAdapter.setBindData(mResultList);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_filter:
                if(mResultList.size()>0) {
                    mIsSearchMode = true;
                    mFilterResultList.clear();
                    String keyword = mEtInput.getText().toString();
                    if(!keyword.isEmpty()) {
                        for(int i = 0; i< mResultList.size(); i++) {
                            if((mResultList.get(i).getPath() + mResultList.get(i).getMethod()).contains(keyword)) {
                                mFilterResultList.add(mResultList.get(i));
                            }
                        }
                        mAdapter.setBindData(mFilterResultList);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.setBindData(mResultList);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(this, "当前无测试结果", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initView() {
        mListView = findViewById(R.id.listview);

        mTestStatus = findViewById(R.id.test_status);
        mEtInput =  findViewById(R.id.et_input_filter);

        mBtnShowAll = findViewById(R.id.btn_showall);
        mBtnShowAll.setOnClickListener(this);
        mBtnStart = findViewById(R.id.btn_start_test);
        mBtnStart.setOnClickListener(this);
        mBtnStop = findViewById(R.id.btn_stop_test);
        mBtnStop.setOnClickListener(this);
        mBtnExport  =  findViewById(R.id.btn_export);
        mBtnExport.setOnClickListener(this);
        mBtnFilter =  findViewById(R.id.btn_filter);
        mBtnFilter.setOnClickListener(this);
    }

    private static class MainHandler extends Handler {
        private WeakReference<MainActivity> mainActivityreference;

        MainHandler(MainActivity activity) {
            mainActivityreference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mainActivityreference.get();
            if(activity.mIndex >= MAX_INDEX) {
                activity.mBtnShowAll.setEnabled(true);
                activity.mSubThread.start();
                return;
            }
            switch (msg.what) {
                case MSG_REFRESH_LIST:
                    List<CarTestResult> list = (List<CarTestResult>) msg.obj;
                    Log.i(TAG, "handleMessage: " + activity.mResultList.size());
                    for(int i = 0; i< list.size(); i++) {
                        if(list.get(i).getModuleTestStatus() != CarTestResult.MODULE_TEST_FINISHED) {
                            activity.mResultList.add(list.get(i));
                        }
                    }
                    activity.mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    Thread mSubThread = new Thread(new Runnable() {
        @Override
        public void run() {
            mExcelUtil.exportExcel("result" + mExcelUtil.getCurrentTimeString() + ".xls", mResultList);
        }
    });

    private void startTest() {
        new ClassCarHvacManagerTest(this).startTest(this);
        new ClassCarCabinManagerTest(this).startTest(this);
        new ClassCarSensorManagerTest(this).startTest(this);
        new ClassCarVendorExtensionManagerTest(this).startTest(this);
        new ClassCarPropertyConfigTest(this).startTest(this);
        new ClassCarPowerManagerTest(this).startTest(this);
        new ClassAppBlockingPackageInfoTest(this).startTest(this::onFinished);
        new ClassCarAppBlockingPolicyTest(this).startTest(this::onFinished);

        new ClassCarTest(this).startTest(this);
        new ClassCarInfoManagerTest(this).startTest(this);
        new ClassCarAppFocusManagerTest(this).startTest(this);
        new ClassCarProjectionManagerTest(this).startTest(this);

        mTestStatus.setText(R.string.test_stop);
    }

    @Override
    public synchronized void onFinished(List<CarTestResult> list) {
        Log.d(TAG, "onFinished: " + list);
        Message msg = new Message();
        msg.what = MSG_REFRESH_LIST;
        msg.obj = list;
        mHandler.sendMessage(msg);
        // Call UI
        // Call Doc
    }
}
