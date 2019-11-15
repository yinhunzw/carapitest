package com.gwm.carapitest.APITest;

import android.car.Car;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.gwm.carapitest.Interfaces.ICarTest;
import com.gwm.carapitest.Interfaces.IListenTest;
import com.gwm.carapitest.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class CarTestBase implements ICarTest{
    public static final String TAG = CarTestBase.class.getSimpleName();
    protected final static boolean DBG = Constants.DBG;
    private final static int CONNECTION_TIME_OUT = 1000;// ms


    protected Context mContext;
    protected Car mCar;
    protected Handler mHandler = new Handler();


    private IListenTest mListenTest;
    private List<CarTestResult> mResultList = new ArrayList<CarTestResult>();
    private List<Runnable> mRunnableList = new ArrayList<>();
    private CarTestResult mModuleTestStatusResult;

    private Runnable mRunnableConnection = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "Car Connection TimeOut", new TimeoutException());
            onTestFinished(CarTestResult.MODULE_TEST_ERROR_CAR_NOT_CONNECTED);
        }
    };
    public CarTestBase(Context context) {
        mContext = context;
        mCar = Car.createCar(mContext, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (DBG) {
                    Log.d(TAG, "onServiceConnected: ");
                }

                mHandler.removeCallbacks(mRunnableConnection);
                onTestStart(mRunnableList);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (DBG) {
                    Log.d(TAG, "onServiceConnected: ");
                }
            }
        });
        mCar.connect();
        mHandler.postDelayed(mRunnableConnection,CONNECTION_TIME_OUT);
        mModuleTestStatusResult = new CarTestResult(getClass()).setModuleTestStatus(CarTestResult.MODULE_TEST_INVALID);
    }
    protected abstract void onTestStart(List<Runnable> runnableList);
    protected abstract void onTestStop();
    protected  void addRunnable(Runnable runnable) {
        mRunnableList.add(runnable);
    }
    protected void onOneTestFinished(CarTestResult carTestResult) {
        if(carTestResult.getMethod() != null) {
            synchronized (mResultList) {
                mResultList.add(carTestResult);
            }
        }

    }
    protected void onTestFinished() {
        onTestFinished(CarTestResult.MODULE_TEST_FINISHED);
    }
    private void onTestFinished(int ModuleTestStatus) {
        mModuleTestStatusResult.setModuleTestStatus(ModuleTestStatus);
        mResultList.add(mModuleTestStatusResult);

        mCar.disconnect();
        mListenTest.onFinished(mResultList);
    }
    public void startTest(IListenTest listenTest) {
        mListenTest = listenTest;
    }

    public void stopTest() {
        onTestStop();
        onTestFinished(CarTestResult.MODULE_TEST_STOPPED);
    }
}
