package com.gwm.carapitest.APITest;

import android.app.Service;
import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.CarProjectionManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Semaphore;

//TBD
public class ClassCarProjectionManagerTest extends CarTestBase {
    private static final String TAG = "ClassCarProjectionManagerTest";

    public CarProjectionManager mCarProjectionManager;
    private TestThread mTestThread;

    public ClassCarProjectionManagerTest(Context context) {
        super(context);


        //addRunnable(()->testSetUnsetListeners());
        //addRunnable(()->testRegisterProjectionRunner());

    }
    private final CarProjectionManager.CarProjectionListener mListener =
            new CarProjectionManager.CarProjectionListener() {
                @Override
                public void onVoiceAssistantRequest(boolean fromLongPress) {
                    //void
                }
            };

    @Override
    protected void onTestStart(List<Runnable> runnableList) {

        try{
            mCarProjectionManager = (CarProjectionManager)mCar.getCarManager(Car.PROJECTION_SERVICE);
        }catch (CarNotConnectedException e){
            e.printStackTrace();
        }

        mTestThread = new TestThread();
        mTestThread.start();

    }

    @Override
    protected void onTestStop() {
        Log.d(TAG, "onTestStop: ");
    }


    public void testSetUnsetListeners() {
        CarTestResult ctr_registerProjectionListener = new CarTestResult(CarProjectionManager.class);
        ctr_registerProjectionListener.setMethod("registerProjectionListener")
                .setIsSystemApi(true).setFeature("注册监听器以监听Projection");
        CarTestResult ctr_unregisterProjectionListener = new CarTestResult(CarProjectionManager.class);
        ctr_unregisterProjectionListener.setMethod("unregisterProjectionListener")
                .setIsSystemApi(true).setFeature("注销监听器停止监听Project事件");
        try {
            mCarProjectionManager.registerProjectionListener(
                    mListener, CarProjectionManager.PROJECTION_VOICE_SEARCH);
            mCarProjectionManager.unregisterProjectionListener();
            ctr_registerProjectionListener.setResult(true);
            ctr_unregisterProjectionListener.setResult(true);

        }catch (Exception e){
            e.printStackTrace();
            ctr_registerProjectionListener.setResult(false).setException(e.toString());
            ctr_unregisterProjectionListener.setResult(false).setException(e.toString());
        }
        onOneTestFinished(ctr_registerProjectionListener);
        onOneTestFinished(ctr_unregisterProjectionListener);
    }

    public void testRegisterProjectionRunner() {

        CarTestResult ctr_registerProjectionRunner = new CarTestResult(CarProjectionManager.class);
        ctr_registerProjectionRunner.setMethod("registerProjectionRunner")
                .setIsSystemApi(true).setFeature("注册监听器以监听Projection");
        CarTestResult ctr_unregisterProjectionRunner = new CarTestResult(CarProjectionManager.class);
        ctr_unregisterProjectionRunner.setMethod("unregisterProjectionRunner")
                .setIsSystemApi(true).setFeature("注销监听器停止监听Project事件");

        Intent intent = new Intent(mContext, TestService.class);
        assertEquals(false,TestService.getBound());
        try {
            mCarProjectionManager.registerProjectionRunner(intent);
            synchronized (TestService.mLock) {
                try {
                    TestService.mLock.wait(1000);
                } catch (InterruptedException e) {
                    // Do nothing
                }
            }

            if (assertEquals(true,TestService.getBound())){
                ctr_registerProjectionRunner.setResult(true);
            }else{
                ctr_registerProjectionRunner.setResult(false);
            }
            mCarProjectionManager.unregisterProjectionRunner(intent);
            ctr_unregisterProjectionRunner.setResult(true);
        }catch (Exception e){
            e.printStackTrace();
            ctr_registerProjectionRunner.setResult(false).setException(e.toString());
            ctr_unregisterProjectionRunner.setResult(false).setException(e.toString());
        }

        onOneTestFinished(ctr_registerProjectionRunner);
        onOneTestFinished(ctr_unregisterProjectionRunner);
        onTestFinished();
    }


    public static class TestService extends Service {
        public static Object mLock = new Object();
        private static boolean sBound;
        private final Binder mBinder = new Binder() {};

        private static synchronized void setBound(boolean bound) {
            sBound = bound;
        }

        public static synchronized boolean getBound() {
            return sBound;
        }

        @Override
        public IBinder onBind(Intent intent) {
            Log.d(TAG, "onBind: setBound");
            setBound(true);
            synchronized (mLock) {
                mLock.notify();
            }
            return mBinder;
        }
    }

    private boolean assertEquals(Object a, Object b) {
        if (a == b) {
            return true;
        } else {
            try {
                throw new Error("expected = " + a + ", actualValue = " + b);
            } catch (Error error) {
                error.printStackTrace();
            }
            return false;
        }
    }
    public class TestThread extends Thread {

        @Override
        public void run() {
            try {
                testSetUnsetListeners();
                testRegisterProjectionRunner();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
