package com.gwm.carapitest.APITest;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarSensorConfig;
import android.car.hardware.CarSensorEvent;
import android.car.hardware.CarSensorManager;
import android.car.hardware.CarVendorExtensionManager;
import android.content.Context;
import android.hardware.automotive.vehicle.V2_0.VehicleArea;
import android.hardware.automotive.vehicle.V2_0.VehicleAreaSeat;
import android.hardware.automotive.vehicle.V2_0.VehiclePropertyGroup;
import android.hardware.automotive.vehicle.V2_0.VehiclePropertyType;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ClassCarVendorExtensionManagerTest extends CarTestBase {
    private static final String TAG = ClassCarVendorExtensionManagerTest.class.getSimpleName();
    private CarVendorExtensionManager mCarVendorExtensionManager;
    private Context mContext;
    private static final int CUSTOM_ZONED_FLOAT_PROP_ID =
            0x2 | VehiclePropertyGroup.VENDOR | VehiclePropertyType.FLOAT | VehicleArea.SEAT;
    private static final int CUSTOM_GLOBAL_INT_PROP_ID =
            0x1 | VehiclePropertyGroup.VENDOR | VehiclePropertyType.INT32 | VehicleArea.GLOBAL;
    private static final float MIN_PROP_FLOAT = 10.42f;
    private static final float MAX_PROP_FLOAT = 42.10f;

    public ClassCarVendorExtensionManagerTest(Context context) {
        super(context);
        mContext = context;
        addRunnable(() -> setProperty(Float.class, CUSTOM_ZONED_FLOAT_PROP_ID, VehicleAreaSeat.ROW_1_RIGHT, MIN_PROP_FLOAT+1 ));
        addRunnable(() -> getProperty(Float.class, CUSTOM_ZONED_FLOAT_PROP_ID, VehicleAreaSeat.ROW_1_RIGHT));
        addRunnable(() -> setGlobalProperty(Integer.class, CUSTOM_GLOBAL_INT_PROP_ID, 0xbeef));
        addRunnable(() -> getGlobalProperty(Integer.class, CUSTOM_GLOBAL_INT_PROP_ID));
        addRunnable(() -> isPropertyAvailable(CUSTOM_ZONED_FLOAT_PROP_ID, VehicleAreaSeat.ROW_1_RIGHT));
        addRunnable(() -> getProperties());
        addRunnable(() -> registerCallback(mCarVendorExtensionCallback));
        addRunnable(() -> unregisterCallback(mCarVendorExtensionCallback));
    }

    private void getProperties() {
        Log.d(TAG, "run: in getProperties()");
        CarTestResult carTestResult = new CarTestResult(mCarVendorExtensionManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        int size = 0;
        try {
            List<CarPropertyConfig> mCarPropertyConfigList = mCarVendorExtensionManager.getProperties();
            size= mCarPropertyConfigList.size();
            for(int i = 0; i< size; i++) {
                Log.i(TAG, mCarPropertyConfigList.get(i).toString());
            }
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取属性列表")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(size > 0);
        onOneTestFinished(carTestResult);
    }

    private <E> void setProperty(Class<E> propertyClass, int propId, int area, E value) {
        Log.d(TAG, "run: in setProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarVendorExtensionManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try{
            mCarVendorExtensionManager.setProperty(propertyClass, propId, area, value);
        } catch(Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("设置指定属性值")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    private <E> void getProperty(Class<Float> propertyClass, int propId, int area) {
        Log.d(TAG, "run: in getProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarVendorExtensionManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        Float value = 0f;
        try{
            value = mCarVendorExtensionManager.getProperty(propertyClass, propId, area);
        } catch(Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取指定属性值")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(MIN_PROP_FLOAT +1 == value ).setExpectedValue(MIN_PROP_FLOAT +1 + "").setTrueValue(value + "");
        onOneTestFinished(carTestResult);
    }

    private <E> void getGlobalProperty(Class<Integer> propertyClass, int propId) {
        Log.d(TAG, "run: in getGlobalProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarVendorExtensionManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        int value = 0;
        try {
            value = mCarVendorExtensionManager.getGlobalProperty(propertyClass, propId);
        } catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取全局属性值")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(0xbeef == value).setTrueValue(value + "").setExpectedValue(0xbeef+ "");
        onOneTestFinished(carTestResult);
    }

    private <E> void setGlobalProperty(Class<E> propertyClass, int propId, E value) {
        Log.d(TAG, "run: in setGlobalProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarVendorExtensionManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCarVendorExtensionManager.setGlobalProperty(propertyClass, propId, value);
        } catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("设置全局属性值")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(exceptionDescription.equals(""));
        onOneTestFinished(carTestResult);
    }

    private void isPropertyAvailable(int propertyId, int area) {
        Log.d(TAG, "run: in isPropertyAvailable()");
        CarTestResult carTestResult = new CarTestResult(mCarVendorExtensionManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean isAvailable = false;
        try {
            isAvailable = mCarVendorExtensionManager.isPropertyAvailable(propertyId, area);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("是否支持指定属性")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(isAvailable).setTrueValue(isAvailable + "").setExpectedValue("true");
        onOneTestFinished(carTestResult);
    }

    private void registerCallback(CarVendorExtensionManager.CarVendorExtensionCallback carVendorExtensionCallback) {
        Log.d(TAG, "run: in registerCallback()");
        CarTestResult carTestResult = new CarTestResult(mCarVendorExtensionManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCarVendorExtensionManager.registerCallback(carVendorExtensionCallback);
        } catch (Exception e) {
            Log.i(TAG, "registerCallback: " + e.toString());
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("注册监听")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    private void unregisterCallback(CarVendorExtensionManager.CarVendorExtensionCallback carVendorExtensionCallback) {
        Log.d(TAG, "run: in unregisterCallback()");
        CarTestResult carTestResult = new CarTestResult(mCarVendorExtensionManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try{
            mCarVendorExtensionManager.unregisterCallback(carVendorExtensionCallback);
        } catch(Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("取消注册指定监听")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(exceptionDescription.equals(""));
        onOneTestFinished(carTestResult);
    }

    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        Log.d(TAG, "onTestStart: ");
        try {
            mCarVendorExtensionManager = (CarVendorExtensionManager) mCar.getCarManager(Car.VENDOR_EXTENSION_SERVICE);
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

    private CarVendorExtensionManager.CarVendorExtensionCallback mCarVendorExtensionCallback = new CarVendorExtensionManager.CarVendorExtensionCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
        }

        @Override
        public void onErrorEvent(int i, int i1) {
        }
    };
}
