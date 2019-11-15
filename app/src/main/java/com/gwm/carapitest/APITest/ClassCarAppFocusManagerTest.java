package com.gwm.carapitest.APITest;

import android.car.Car;
import android.car.CarAppFocusManager;
import android.car.CarNotConnectedException;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.car.CarAppFocusManager.APP_FOCUS_TYPE_NAVIGATION;
import static android.car.CarAppFocusManager.APP_FOCUS_TYPE_VOICE_COMMAND;

public class ClassCarAppFocusManagerTest extends CarTestBase {

    private static final String TAG = "ClassCarAppFocusManagerTest";
    private static final long DEFAULT_WAIT_TIMEOUT_MS = 1000;

    private CarAppFocusManager mManager;
    private TestThread mTestThread;

    private final LooperThread mEventThread = new LooperThread();

    public ClassCarAppFocusManagerTest(Context context) {
        super(context);
    }

    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        try {
            mManager = (CarAppFocusManager) mCar.getCarManager(Car.APP_FOCUS_SERVICE);
            int[] activeTypes = mManager.getActiveAppTypes();
            FocusOwnershipCallback owner = new FocusOwnershipCallback();
            for (int i = 0; i < activeTypes.length; i++) {
                mManager.requestAppFocus(activeTypes[i], owner);
                owner.waitForOwnershipGrant(DEFAULT_WAIT_TIMEOUT_MS, activeTypes[i]);
                mManager.abandonAppFocus(owner, activeTypes[i]);
                owner.waitForOwnershipLoss(
                        DEFAULT_WAIT_TIMEOUT_MS, activeTypes[i]);
            }
            mEventThread.start();
            mEventThread.waitForReadyState();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (Runnable run : runnableList) {
//            run.run();
//        }
//        onTestFinished();
        mTestThread = new TestThread();
        mTestThread.start();
    }

    @Override
    protected void onTestStop() {
        Log.d(TAG, "onTestStop: ");
    }

    /**
     * method public void abandonAppFocus(android.car.CarAppFocusManager.OnAppFocusOwnershipCallback, int);
     * method public void abandonAppFocus(android.car.CarAppFocusManager.OnAppFocusOwnershipCallback);
     * method public void addFocusListener(android.car.CarAppFocusManager.OnAppFocusChangedListener, int) throws android.car.CarNotConnectedException;
     * method public boolean isOwningFocus(android.car.CarAppFocusManager.OnAppFocusOwnershipCallback, int) throws android.car.CarNotConnectedException;
     * method public void removeFocusListener(android.car.CarAppFocusManager.OnAppFocusChangedListener, int);
     * method public void removeFocusListener(android.car.CarAppFocusManager.OnAppFocusChangedListener);
     * method public int requestAppFocus(int, android.car.CarAppFocusManager.OnAppFocusOwnershipCallback) throws android.car.CarNotConnectedException, java.lang.SecurityException;
     */

    public void testRegisterUnregister() {
        Log.d(TAG, "testRegisterUnregister: start");

        CarTestResult ctr_requestAppFocus = new CarTestResult(CarAppFocusManager.class);
        ctr_requestAppFocus.setMethod("requestAppFocus").setIsSystemApi(false).setFeature("请求应用焦点");
        CarTestResult ctr_addFocusListener = new CarTestResult(CarAppFocusManager.class);
        ctr_addFocusListener.setMethod("addFocusListener").setIsSystemApi(false).setFeature("注册监听应用焦点变化");
        CarTestResult ctr_removeFocusListener = new CarTestResult(CarAppFocusManager.class);
        ctr_removeFocusListener.setMethod("removeFocusListener").setIsSystemApi(false).setFeature("注销监听焦点变化");

        FocusChangedListener listener1 = new FocusChangedListener();
        FocusChangedListener listener2 = new FocusChangedListener();

        int value = CarAppFocusManager.APP_FOCUS_REQUEST_FAILED;
        try {
            CarAppFocusManager manager = createManager();
            manager.addFocusListener(listener1, APP_FOCUS_TYPE_NAVIGATION);
            manager.addFocusListener(listener2, APP_FOCUS_TYPE_NAVIGATION);
            manager.addFocusListener(listener2, APP_FOCUS_TYPE_VOICE_COMMAND);

            manager.removeFocusListener(listener1, APP_FOCUS_TYPE_NAVIGATION);

            value = manager.requestAppFocus(APP_FOCUS_TYPE_NAVIGATION, new FocusOwnershipCallback());
            if (assertEquals(CarAppFocusManager.APP_FOCUS_REQUEST_SUCCEEDED, value)) {
                ctr_requestAppFocus.setResult(true).setExpectedValue("1").setTrueValue(Integer.toString(value));
            } else {
                ctr_requestAppFocus.setResult(false).setExpectedValue("1").setTrueValue(Integer.toString(value));
            }


            // remove listener, no event expected
            boolean isNeedListen = listener1.waitForFocusChange(
                    DEFAULT_WAIT_TIMEOUT_MS, APP_FOCUS_TYPE_NAVIGATION, true);
            if (assertEquals(false, isNeedListen)) {
                ctr_removeFocusListener.setResult(true);
            } else {
                ctr_removeFocusListener.setResult(false);
            }

            // still listen event
            isNeedListen = listener2.waitForFocusChange(
                    DEFAULT_WAIT_TIMEOUT_MS, APP_FOCUS_TYPE_NAVIGATION, true);
            if (assertEquals(true, isNeedListen)) {
                ctr_addFocusListener.setResult(true);
            } else {
                ctr_addFocusListener.setResult(false);
            }

            manager.removeFocusListener(listener2, APP_FOCUS_TYPE_VOICE_COMMAND);
            manager.removeFocusListener(listener2, APP_FOCUS_TYPE_VOICE_COMMAND);// Double-unregister is OK
        } catch (Exception e) {
            e.printStackTrace();
            ctr_requestAppFocus.setResult(false).setExpectedValue("1").setTrueValue(Integer.toString(value))
                    .setException(e.toString());
            ctr_removeFocusListener.setResult(false).setException(e.toString());
            ctr_addFocusListener.setResult(false).setException(e.toString());

            onOneTestFinished(ctr_requestAppFocus);
            onOneTestFinished(ctr_addFocusListener);
            onOneTestFinished(ctr_removeFocusListener);
        }

        onOneTestFinished(ctr_requestAppFocus);
        onOneTestFinished(ctr_addFocusListener);
        onOneTestFinished(ctr_removeFocusListener);

    }

    public void testFocusChange() {

        CarTestResult ctr_getActiveAppTypes = new CarTestResult(CarAppFocusManager.class);
        ctr_getActiveAppTypes.setMethod("getActiveAppTypes").setIsSystemApi(false).setFeature("获取当前活跃的应用类型");
        CarTestResult ctr_isOwningFocus = new CarTestResult(CarAppFocusManager.class);
        ctr_isOwningFocus.setMethod("isOwningFocus").setIsSystemApi(false).setFeature("判断listener 与活动焦点是否相关");
        CarTestResult ctr_abandonAppFocus = new CarTestResult(CarAppFocusManager.class);
        ctr_abandonAppFocus.setMethod("abandonAppFocus").setIsSystemApi(false).setFeature("放弃应用焦点");

        try {
            CarAppFocusManager manager1 = createManager();
            CarAppFocusManager manager2 = createManager();
            //assertNotNull(manager2);
            final int[] emptyFocus = new int[0];

            //Assert.assertArrayEquals(emptyFocus, manager1.getActiveAppTypes());
            FocusChangedListener change1 = new FocusChangedListener();
            FocusChangedListener change2 = new FocusChangedListener();
            FocusOwnershipCallback owner1 = new FocusOwnershipCallback();
            FocusOwnershipCallback owner2 = new FocusOwnershipCallback();
            manager1.addFocusListener(change1, APP_FOCUS_TYPE_NAVIGATION);
            manager1.addFocusListener(change1, APP_FOCUS_TYPE_VOICE_COMMAND);
            manager2.addFocusListener(change2, APP_FOCUS_TYPE_NAVIGATION);
            manager2.addFocusListener(change2, APP_FOCUS_TYPE_VOICE_COMMAND);


            assertEquals(CarAppFocusManager.APP_FOCUS_REQUEST_SUCCEEDED,
                    manager1.requestAppFocus(APP_FOCUS_TYPE_NAVIGATION, owner1));
            assertEquals(true, owner1.waitForOwnershipGrant(
                    DEFAULT_WAIT_TIMEOUT_MS, APP_FOCUS_TYPE_NAVIGATION));
            int[] expectedFocuses = new int[]{APP_FOCUS_TYPE_NAVIGATION};
            if (!Arrays.equals(expectedFocuses, manager1.getActiveAppTypes())
                    || !Arrays.equals(expectedFocuses, manager2.getActiveAppTypes())) {
                ctr_getActiveAppTypes.setResult(false);
            }else{
                ctr_getActiveAppTypes.setResult(true);
            }
            if (!assertEquals(true, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(false, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_VOICE_COMMAND))
                    || !assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_VOICE_COMMAND))) {
                ctr_isOwningFocus.setResult(false);
            }else {
                ctr_isOwningFocus.setResult(true);
            }

            assertEquals(true, change2.waitForFocusChange(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_NAVIGATION, true));
            assertEquals(true, change1.waitForFocusChange(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_NAVIGATION, true));

            assertEquals(CarAppFocusManager.APP_FOCUS_REQUEST_SUCCEEDED,
                    manager1.requestAppFocus(APP_FOCUS_TYPE_VOICE_COMMAND, owner1));
            assertEquals(true, owner1.waitForOwnershipGrant(
                    DEFAULT_WAIT_TIMEOUT_MS, APP_FOCUS_TYPE_VOICE_COMMAND));
            expectedFocuses = new int[]{
                    APP_FOCUS_TYPE_NAVIGATION,
                    APP_FOCUS_TYPE_VOICE_COMMAND};

            if (!assertEquals(true, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(true, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_VOICE_COMMAND))
                    || !assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_VOICE_COMMAND))) {
                ctr_isOwningFocus.setResult(false);
            }else {
                ctr_isOwningFocus.setResult(true);
            }

            if (!Arrays.equals(expectedFocuses, manager1.getActiveAppTypes())
                    || !Arrays.equals(expectedFocuses, manager2.getActiveAppTypes())) {
                ctr_getActiveAppTypes.setResult(false);
            }else {
                ctr_getActiveAppTypes.setResult(true);
            }

            assertEquals(true, change2.waitForFocusChange(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_VOICE_COMMAND, true));
            assertEquals(true, change1.waitForFocusChange(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_VOICE_COMMAND, true));

            // this should be no-op
            change1.reset();
            change2.reset();
            assertEquals(CarAppFocusManager.APP_FOCUS_REQUEST_SUCCEEDED,
                    manager1.requestAppFocus(APP_FOCUS_TYPE_NAVIGATION, owner1));
            assertEquals(true, owner1.waitForOwnershipGrant(
                    DEFAULT_WAIT_TIMEOUT_MS, APP_FOCUS_TYPE_NAVIGATION));

            //Assert.assertArrayEquals(expectedFocuses, manager1.getActiveAppTypes());
            //Assert.assertArrayEquals(expectedFocuses, manager2.getActiveAppTypes());
            assertEquals(false, change2.waitForFocusChange(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_NAVIGATION, true));
            assertEquals(false, change1.waitForFocusChange(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_NAVIGATION, true));

            //manager2 获取 NAVIGATION 焦点后， manager1 则失去该类型焦点
            assertEquals(CarAppFocusManager.APP_FOCUS_REQUEST_SUCCEEDED,
                    manager2.requestAppFocus(APP_FOCUS_TYPE_NAVIGATION, owner2));
            assertEquals(true, owner2.waitForOwnershipGrant(
                    DEFAULT_WAIT_TIMEOUT_MS, APP_FOCUS_TYPE_NAVIGATION));

            //验证焦点owning 状态
            if (!assertEquals(false, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(true, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_VOICE_COMMAND))
                    || !assertEquals(true, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_VOICE_COMMAND))) {
                ctr_isOwningFocus.setResult(false);
            }else {
                ctr_isOwningFocus.setResult(true);
            }

            if (!Arrays.equals(expectedFocuses, manager1.getActiveAppTypes())
                    || !Arrays.equals(expectedFocuses, manager2.getActiveAppTypes())) {
                ctr_getActiveAppTypes.setResult(false);
            }else {
                ctr_getActiveAppTypes.setResult(true);
            }
            assertEquals(true, owner1.waitForOwnershipLoss(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_NAVIGATION));

            // no-op as it is not owning it
            change1.reset();
            change2.reset();
            manager1.abandonAppFocus(owner1, APP_FOCUS_TYPE_NAVIGATION);

            if (!assertEquals(false, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(true, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_VOICE_COMMAND))
                    || !assertEquals(true, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_VOICE_COMMAND))) {
                ctr_isOwningFocus.setResult(false);
            }else {
                ctr_isOwningFocus.setResult(true);
            }

            if (!Arrays.equals(expectedFocuses, manager1.getActiveAppTypes())
                    || !Arrays.equals(expectedFocuses, manager2.getActiveAppTypes())) {
                ctr_getActiveAppTypes.setResult(false);
            }else {
                ctr_getActiveAppTypes.setResult(true);
            }

            change1.reset();
            change2.reset();
            manager1.abandonAppFocus(owner1, APP_FOCUS_TYPE_VOICE_COMMAND);
            if (!assertEquals(false, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(false, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_VOICE_COMMAND))
                    || !assertEquals(true, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_VOICE_COMMAND))) {
                ctr_isOwningFocus.setResult(false);
            }else {
                ctr_isOwningFocus.setResult(true);
            }
            if(assertEquals(false, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_VOICE_COMMAND))){
                ctr_abandonAppFocus.setResult(true);
            }else{
                ctr_abandonAppFocus.setResult(false);
            }

            expectedFocuses = new int[]{APP_FOCUS_TYPE_NAVIGATION};
            if (!Arrays.equals(expectedFocuses, manager1.getActiveAppTypes())
                    || !Arrays.equals(expectedFocuses, manager2.getActiveAppTypes())) {
                ctr_getActiveAppTypes.setResult(false);
            }

            assertEquals(true, change2.waitForFocusChange(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_VOICE_COMMAND, false));
            assertEquals(true, change1.waitForFocusChange(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_VOICE_COMMAND, false));

            change1.reset();
            change2.reset();
            manager2.abandonAppFocus(owner2, APP_FOCUS_TYPE_NAVIGATION);
            if (!assertEquals(false, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(false, manager1.isOwningFocus(owner1, APP_FOCUS_TYPE_VOICE_COMMAND))
                    || !assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_NAVIGATION))
                    || !assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_VOICE_COMMAND))) {
                ctr_isOwningFocus.setResult(false);
            }else {
                ctr_isOwningFocus.setResult(true);
            }
            if(assertEquals(false, manager2.isOwningFocus(owner2, APP_FOCUS_TYPE_NAVIGATION))){
                ctr_abandonAppFocus.setResult(true);
            }else{
                ctr_abandonAppFocus.setResult(false);
            }

            expectedFocuses = emptyFocus;
            if (!Arrays.equals(expectedFocuses, manager1.getActiveAppTypes())
                    || !Arrays.equals(expectedFocuses, manager2.getActiveAppTypes())) {
                ctr_getActiveAppTypes.setResult(false);
            }else {
                ctr_getActiveAppTypes.setResult(true);
            }
            assertEquals(true, change1.waitForFocusChange(DEFAULT_WAIT_TIMEOUT_MS,
                    APP_FOCUS_TYPE_NAVIGATION, false));

            manager1.removeFocusListener(change1);
            manager2.removeFocusListener(change2);
        } catch (Exception e) {
            e.printStackTrace();
            ctr_abandonAppFocus.setResult(false).setException(e.toString());
            ctr_getActiveAppTypes.setResult(false).setException(e.toString());
            ctr_isOwningFocus.setResult(false).setException(e.toString());
        }
        onOneTestFinished(ctr_abandonAppFocus);
        onOneTestFinished(ctr_getActiveAppTypes);
        onOneTestFinished(ctr_isOwningFocus);
    }


    private class FocusChangedListener implements CarAppFocusManager.OnAppFocusChangedListener {
        private volatile int mLastChangeAppType;
        private volatile boolean mLastChangeAppActive;
        private volatile Semaphore mChangeWait = new Semaphore(0);

        boolean waitForFocusChange(long timeoutMs, int expectedAppType,
                                   boolean expectedAppActive) throws Exception {

            if (!mChangeWait.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
                return false;
            }

            if (assertEquals(expectedAppType, mLastChangeAppType)
                    && assertEquals(expectedAppActive, mLastChangeAppActive)) {
                return true;
            } else {
                return false;
            }
        }

        void reset() throws InterruptedException {
            mLastChangeAppType = 0;
            mLastChangeAppActive = false;
            mChangeWait.drainPermits();
        }

        @Override
        public void onAppFocusChanged(int appType, boolean active) {
            Log.d(TAG, "onAppFocusChanged: ");
            //assertEventThread();
            mLastChangeAppType = appType;
            mLastChangeAppActive = active;
            mChangeWait.release();
        }
    }

    private class FocusOwnershipCallback
            implements CarAppFocusManager.OnAppFocusOwnershipCallback {
        private int mLastLossEvent;
        private final Semaphore mLossEventWait = new Semaphore(0);
        private int mLastGrantEvent;
        private final Semaphore mGrantEventWait = new Semaphore(0);

        boolean waitForOwnershipLoss(long timeoutMs, int expectedAppType)
                throws Exception {
            if (!mLossEventWait.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
                return false;
            }
            return assertEquals(expectedAppType, mLastLossEvent);
        }

        boolean waitForOwnershipGrant(long timeoutMs, int expectedAppType)
                throws Exception {
            if (!mGrantEventWait.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
                return false;
            }
            return assertEquals(expectedAppType, mLastGrantEvent);
        }

        @Override
        public void onAppFocusOwnershipLost(int appType) {
            Log.i(TAG, "onAppFocusOwnershipLost " + appType);
            //assertEventThread();
            mLastLossEvent = appType;
            mLossEventWait.release();
        }

        @Override
        public void onAppFocusOwnershipGranted(int appType) {
            Log.i(TAG, "onAppFocusOwnershipGranted " + appType);
            mLastGrantEvent = appType;
            mGrantEventWait.release();
        }
    }

    private static class LooperThread extends Thread {

        private final Object mReadySync = new Object();
        volatile Handler mHandler;

        @Override
        public void run() {
            Looper.prepare();
            mHandler = new Handler();

            synchronized (mReadySync) {
                mReadySync.notifyAll();
            }

            Looper.loop();
        }

        void waitForReadyState() throws InterruptedException {
            synchronized (mReadySync) {
                mReadySync.wait(DEFAULT_WAIT_TIMEOUT_MS);
            }
        }
    }

    private CarAppFocusManager createManager()
            throws CarNotConnectedException, InterruptedException {
        return createManager(mContext, mEventThread);
    }

    private static CarAppFocusManager createManager(Context context,
                                                    LooperThread eventThread) throws InterruptedException, CarNotConnectedException {
        Car car = createCar(context, eventThread);
        CarAppFocusManager manager = (CarAppFocusManager) car.getCarManager(Car.APP_FOCUS_SERVICE);
        //assertNotNull(manager);
        return manager;
    }

    private static Car createCar(Context context, LooperThread eventThread)
            throws InterruptedException {
        DefaultServiceConnectionListener connectionListener =
                new DefaultServiceConnectionListener();
        Car car = Car.createCar(context, connectionListener, eventThread.mHandler);
        //assertNotNull(car);
        car.connect();
        connectionListener.waitForConnection(DEFAULT_WAIT_TIMEOUT_MS);
        return car;
    }

    protected static class DefaultServiceConnectionListener implements ServiceConnection {
        private final Semaphore mConnectionWait = new Semaphore(0);

        public void waitForConnection(long timeoutMs) throws InterruptedException {
            mConnectionWait.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //assertMainThread();
            mConnectionWait.release();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //assertMainThread();
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
                testRegisterUnregister();
                testFocusChange();
                onTestFinished();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
