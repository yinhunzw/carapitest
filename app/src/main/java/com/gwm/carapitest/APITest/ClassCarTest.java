package com.gwm.carapitest.APITest;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.ICar;
import android.car.hardware.CarSensorManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ClassCarTest extends CarTestBase {
    private static final String TAG = ClassCarTest.class.getSimpleName();

    private static final long DEFAULT_WAIT_TIMEOUT_MS = 1000;

    private final Semaphore mConnectionWait = new Semaphore(0);
    private TestThread mTestThread;

    public ClassCarTest(Context context) {
        super(context);
    }

    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        Log.d(TAG, "onTestStart: ");

        mTestThread = new TestThread();
        mTestThread.start();

    }

    @Override
    protected void onTestStop() {
        Log.d(TAG, "onTestStop: ");
    }


    private final ServiceConnection mConnectionListener = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (Looper.getMainLooper().isCurrentThread()) {
                Log.d(TAG, "onServiceDisconnected: this is Main thread !");
            } else {
                Log.d(TAG, "this is NOT Main thread !");
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (Looper.getMainLooper().isCurrentThread()) {
                Log.d(TAG, "onServiceConnected: this is Main thread !");
            }
            mConnectionWait.release();
            Log.d(TAG, "onServiceConnected: released!!");
        }
    };

    private void waitForConnection(long timeoutMs) throws InterruptedException {
        mConnectionWait.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
    }

    public void testCarConnection() throws Exception {

        CarTestResult ctr_createCar = new CarTestResult(Car.class);
        ctr_createCar.setMethod("createCar").setIsSystemApi(false).setFeature("创建Car实例");
        CarTestResult ctr_connect = new CarTestResult(Car.class);
        ctr_connect.setMethod("connect").setIsSystemApi(false).setFeature("连接Car Service");
        CarTestResult ctr_disconnect = new CarTestResult(Car.class);
        ctr_disconnect.setMethod("disconnect").setIsSystemApi(false).setFeature("断开Car Service");
        CarTestResult ctr_isConnected = new CarTestResult(Car.class);
        ctr_isConnected.setMethod("isConnected").setIsSystemApi(false).setFeature("Car Service 是否已连接状态");
        CarTestResult ctr_connecting = new CarTestResult(Car.class);
        ctr_connecting.setMethod("isConnecting").setIsSystemApi(false).setFeature("Car Service 是否正在连接状态");
        CarTestResult ctr_getCarManager = new CarTestResult(Car.class);
        ctr_getCarManager.setMethod("getCarManager").setIsSystemApi(false).setFeature("获取某个CarManager");
        CarTestResult ctr_getCarConnectionType = new CarTestResult(Car.class);
        ctr_getCarConnectionType.setMethod("getCarConnectionType").setIsSystemApi(false).setFeature("获取汽车连接类型");

        Car car = Car.createCar(mContext, mConnectionListener);
        String val = assertEquals(null, car);
        if ("true".equals(val)) {
            ctr_createCar.setResult(false).setException("").setComment("Can't create Car instance");
        } else {
            ctr_createCar.setResult(true);
        }
        onOneTestFinished(ctr_createCar);

        val = assertEquals(false, car.isConnected());
        if (!"true".equals(val)) {
            ctr_isConnected.setResult(false).setException(val).setComment(val);
            onOneTestFinished(ctr_isConnected);
        }
        val = assertEquals(false, car.isConnecting());
        if (!"true".equals(val)) {
            ctr_connecting.setResult(false).setException(val).setComment(val);
            onOneTestFinished(ctr_connecting);
        }

        car.connect();
        waitForConnection(DEFAULT_WAIT_TIMEOUT_MS);

        val = assertEquals(true, car.isConnected());
        if (!"true".equals(val)) {
            ctr_isConnected.setResult(false).setException(val).setComment(val);
            onOneTestFinished(ctr_isConnected);
        } else {
            ctr_connect.setResult(true);
            onOneTestFinished(ctr_connect);
        }
        val = assertEquals(false, car.isConnecting());
        if (!"true".equals(val)) {
            ctr_connecting.setResult(false).setException(val).setComment(val);
            onOneTestFinished(ctr_connecting);
        }

        CarSensorManager carSensorManager =
                (CarSensorManager) car.getCarManager(Car.SENSOR_SERVICE);
        CarSensorManager carSensorManager2 =
                (CarSensorManager) car.getCarManager(Car.SENSOR_SERVICE);
        assertEquals(carSensorManager, carSensorManager2);
        Object noSuchService = car.getCarManager("No such service");
        assertEquals(null, noSuchService);
        // double disconnect should be safe.
        car.disconnect();
        car.disconnect();

        val = assertEquals(false, car.isConnected());
        if (!"true".equals(val)) {
            ctr_isConnected.setResult(false).setException(val).setComment(val);
            onOneTestFinished(ctr_isConnected);
        } else {
            ctr_disconnect.setResult(true);
            ctr_isConnected.setResult(true);
            onOneTestFinished(ctr_isConnected);
            onOneTestFinished(ctr_disconnect);
        }
        val = assertEquals(false, car.isConnecting());
        if (!"true".equals(val)) {
            ctr_connecting.setResult(false).setException(val).setComment(val);
        } else {
            ctr_connecting.setResult(true);
        }
        onOneTestFinished(ctr_connecting);

        val = assertEquals(Car.CONNECTION_TYPE_EMBEDDED, car.getCarConnectionType());
        if (!"true".equals(val)) {
            ctr_getCarConnectionType.setResult(false).setException(val).setComment(val);
        } else {
            ctr_getCarConnectionType.setResult(true);
        }
        onOneTestFinished(ctr_getCarConnectionType);

        onTestFinished();
    }

    /**
     * this is just test equal or not
     *
     * @param a expectedValue, only support int , float, boolean etc for "==", others types should use "equal"
     * @param b actualValue, only support int , float, boolean etc for "==", others types should use "equal"
     * @return
     */
    private String assertEquals(Object a, Object b) {
        if (a == b) {
            return "true";
        } else {
            try {
                throw new Error("expected = " + a + ", actualValue = " + b);
            } catch (Error e) {
                e.printStackTrace();
                return e.toString();
            }

        }
    }

    public class TestThread extends Thread {

        @Override
        public void run() {
            try {
                testCarConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
