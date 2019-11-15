package com.gwm.carapitest.APITest;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.VehicleAreaSeat;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarSensorConfig;
import android.car.hardware.CarSensorEvent;
import android.car.hardware.CarSensorManager;
import android.car.hardware.cabin.CarCabinManager;
import android.content.Context;
import android.hardware.automotive.vehicle.V2_0.VehicleAreaDoor;
import android.hardware.automotive.vehicle.V2_0.VehicleAreaWindow;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ClassCarCabinManagerTest extends CarTestBase {
    private static final String TAG = ClassCarSensorManagerTest.class.getSimpleName();
    private CarCabinManager mCabinManager;
    private Semaphore mAvailable;
    private Context mContext;
    private boolean mEventBoolVal;
    private int mEventIntVal;
    private int mEventZoneVal;

    public ClassCarCabinManagerTest(Context context) {
        super(context);
        mContext = context;
        addRunnable(() -> getPropertyList());
//        addRunnable(() -> setBooleanProperty(CarCabinManager.ID_DOOR_LOCK,
//                VehicleAreaDoor.ROW_1_LEFT, true));
        addRunnable(() -> getBooleanProperty(CarCabinManager.ID_DOOR_LOCK,
                VehicleAreaDoor.ROW_1_LEFT));
        addRunnable(() ->setIntProperty(CarCabinManager.ID_WINDOW_POS,
                VehicleAreaWindow.ROW_1_LEFT, 50));
        addRunnable(() ->getIntProperty(CarCabinManager.ID_WINDOW_POS,
                VehicleAreaWindow.ROW_1_LEFT));
        addRunnable(() -> registerCallback(mCarCabinEventCallback));
        addRunnable(() -> unregisterCallback(mCarCabinEventCallback));
    }

    private void getPropertyList() {
        Log.d(TAG, "run: in getPropertyList()");
        CarTestResult carTestResult = new CarTestResult(mCabinManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        int size = 0;
        List<CarPropertyConfig> carPropertyConfigList = new ArrayList<>();
        try {
            carPropertyConfigList = mCabinManager.getPropertyList();
            size= carPropertyConfigList.size();
            for(int i = 0; i< size; i++) {
                Log.i(TAG, carPropertyConfigList.get(i).toString());
            }
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取属性列表")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(size > 0).setTrueValue(carPropertyConfigList.toString());
        onOneTestFinished(carTestResult);
    }

    private void setBooleanProperty(int propertyId, int area, boolean val) {
        Log.d(TAG, "run: in setBooleanProperty()");
        CarTestResult carTestResult = new CarTestResult(mCabinManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCabinManager.setBooleanProperty(CarCabinManager.ID_DOOR_LOCK,
                    VehicleAreaDoor.ROW_1_LEFT, true);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    private void getBooleanProperty(int propertyId, int area) {
        Log.d(TAG, "run: in getBooleanProperty()");
        CarTestResult carTestResult = new CarTestResult(mCabinManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean value = false;
        try {
            value = mCabinManager.getBooleanProperty(CarCabinManager.ID_DOOR_LOCK,
                    VehicleAreaDoor.ROW_1_LEFT);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(value).setExpectedValue("true").setTrueValue(value+ "");
        onOneTestFinished(carTestResult);
    }

    private void setIntProperty(int propertyId, int area, int val) {
        Log.d(TAG, "run: in setIntProperty()");
        CarTestResult carTestResult = new CarTestResult(mCabinManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCabinManager.setIntProperty(propertyId, area, val);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    private void getIntProperty(int propertyId, int area) {
        Log.d(TAG, "run: in getIntProperty()");
        CarTestResult carTestResult = new CarTestResult(mCabinManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        int value = 0;
        try {
            value = mCabinManager.getIntProperty(propertyId, area);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(value == 50).setExpectedValue("50").setTrueValue(value + "");
        onOneTestFinished(carTestResult);
    }

    private void registerCallback(CarCabinManager.CarCabinEventCallback callback) {
        Log.d(TAG, "run: in registerCallback()");
        CarTestResult carTestResult = new CarTestResult(mCabinManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCabinManager.registerCallback(callback);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("注册监听")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    private void unregisterCallback(CarCabinManager.CarCabinEventCallback callback) {
        Log.d(TAG, "run: in unregisterCallback()");
        CarTestResult carTestResult = new CarTestResult(mCabinManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean result = true;
        try{
            mCabinManager.unregisterCallback(callback);
        } catch(Exception e) {
            result = false;
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("取消注册监听")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(result);
        onOneTestFinished(carTestResult);
    }

    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        Log.d(TAG, "onTestStart: ");
        try {
            mCabinManager = (CarCabinManager) mCar.getCarManager(Car.CABIN_SERVICE);
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

    }

    private CarCabinManager.CarCabinEventCallback mCarCabinEventCallback =new CarCabinManager.CarCabinEventCallback() {

        @Override
        public void onChangeEvent(final CarPropertyValue value) {
            Log.d(TAG, "onChangeEvent: "  + value);
            Object o = value.getValue();
            mEventZoneVal = value.getAreaId();

            if (o instanceof Integer) {
                mEventIntVal = (Integer) o;
            } else if (o instanceof Boolean) {
                mEventBoolVal = (Boolean) o;
            } else {
                Log.e(TAG, "onChangeEvent:  Unknown instance type = " + o.getClass().getName());
            }
            mAvailable.release();
        }

        @Override
        public void onErrorEvent(final int propertyId, final int zone) {
            Log.d(TAG, "Error:  propertyId=" + propertyId + "  zone=" + zone);
        }
    };
}
