package com.gwm.carapitest.APITest;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.power.CarPowerManager;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import java.util.List;
import java.util.concurrent.Executor;

public class ClassCarPowerManagerTest extends CarTestBase {
    private static final String TAG = ClassCarPowerManagerTest.class.getSimpleName();
    private CarPowerManager mCarPowerManager;
    private Context mContext;
    private Executor mExecutor;
    private SparseArray<String> mBootReason = new SparseArray<>();

    public ClassCarPowerManagerTest(Context context) {
        super(context);
        mContext = context;
        mBootReason.append(1, "BOOT_REASON_USER_POWER_ON");
        mBootReason.append(2, "BOOT_REASON_DOOR_UNLOCK");
        mBootReason.append(3, "BOOT_REASON_TIMER");
        mBootReason.append(4, "BOOT_REASON_DOOR_OPEN");
        mBootReason.append(5, "BOOT_REASON_REMOTE_START");

        mExecutor = new ThreadPerTaskExecutor();
        addRunnable(() -> requestShutdownOnNextSuspend());
        addRunnable(this::getBootReason);
        addRunnable(() -> setListener(mPowerListener, mExecutor));
        addRunnable(this::clearListener);
    }

    private void requestShutdownOnNextSuspend() {
        Log.d(TAG, "run: in requestShutdownOnNextSuspend()");
        CarTestResult carTestResult = new CarTestResult(mCarPowerManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCarPowerManager.requestShutdownOnNextSuspend();
        } catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    private void getBootReason() {
        Log.d(TAG, "run: in getBootReason()");
        CarTestResult carTestResult = new CarTestResult(mCarPowerManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        int bootReason = 0;
        try {
            bootReason = mCarPowerManager.getBootReason();
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获得Boot原因")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(bootReason != 0).setTrueValue(bootReason + " : " + mBootReason.get(bootReason)).setExpectedValue("1~5");
        onOneTestFinished(carTestResult);
    }

    private void setListener(CarPowerManager.CarPowerStateListener listener, Executor executor) {
        Log.d(TAG, "run: in registerCallback()");
        CarTestResult carTestResult = new CarTestResult(mCarPowerManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean registerResult = false;
        try {
            mCarPowerManager.setListener(listener, executor);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
            Log.e(TAG, "Car is not connected!");
        } catch (IllegalStateException e) {
            exceptionDescription.append(e.toString());
            Log.e(TAG, "CarPowerManager listener was not cleared");
        }
        carTestResult.setFeature("注册监听")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    private void clearListener() {
        Log.d(TAG, "run: in clearListener()");
        CarTestResult carTestResult = new CarTestResult(mCarPowerManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try{
            mCarPowerManager.clearListener();
        } catch(Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("取消注册监听")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        Log.d(TAG, "onTestStart: ");
        try {
            mCarPowerManager = (CarPowerManager) mCar.getCarManager(Car.POWER_SERVICE);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
        for (Runnable run:runnableList) {
            run.run();
        }
        onTestFinished();
    }

    @Override
    protected void onTestStop() {
        Log.d(TAG, "onTestStop: ");
    }

    private final CarPowerManager.CarPowerStateListener mPowerListener =
            new CarPowerManager.CarPowerStateListener () {
                @Override
                public void onStateChanged(int state) {
                    Log.i(TAG, "onStateChanged() state = " + state);
                }
            };

    private class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }
}
