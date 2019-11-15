package com.gwm.carapitest.APITest;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.VehicleAreaSeat;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarSensorConfig;
import android.car.hardware.CarSensorEvent;
import android.car.hardware.CarSensorManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ClassCarSensorManagerTest extends CarTestBase {
    private static final String TAG = ClassCarSensorManagerTest.class.getSimpleName();
    private CarSensorManager mCarSensorManager;
    private Context mContext;

    public ClassCarSensorManagerTest(Context context) {
        super(context);
        mContext = context;
        addRunnable(() -> getPropertyList());
        addRunnable(() -> getLatestSensorEvent(CarSensorManager.SENSOR_TYPE_CAR_SPEED));
        addRunnable(() -> getSensorConfig(CarSensorManager.SENSOR_TYPE_CAR_SPEED));
        addRunnable(() -> isSensorSupported(CarSensorManager.SENSOR_TYPE_CAR_SPEED));
        addRunnable(() -> registerListener(mCarSpeedChangedListener, CarSensorManager.SENSOR_TYPE_CAR_SPEED, CarSensorManager.SENSOR_RATE_NORMAL));
        addRunnable(() -> unregisterListener(mCarSpeedChangedListener, CarSensorManager.SENSOR_TYPE_CAR_SPEED));
        addRunnable(() -> unregisterListener(mCarSpeedChangedListener));
        addRunnable(() -> getSupportedSensors());
    }

    private void getPropertyList() {
        Log.d(TAG, "run: in getPropertyList()");
        CarTestResult carTestResult = new CarTestResult(mCarSensorManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        int size = 0;
        List<CarPropertyConfig> carPropertyConfigList = new ArrayList<>();
        try {
            carPropertyConfigList = mCarSensorManager.getPropertyList();
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

    private void getSensorConfig(int type) {
        Log.d(TAG, "run: in getSensorConfig()");
        CarTestResult carTestResult = new CarTestResult(mCarSensorManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        CarSensorConfig carSensorConfig = null;
        try {
            carSensorConfig = mCarSensorManager.getSensorConfig(type);
                Log.i(TAG, carSensorConfig.toString());
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取车速传感器配置")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(carSensorConfig != null).setTrueValue(carSensorConfig == null ? "null" : carSensorConfig.toString());
        onOneTestFinished(carTestResult);
    }

    private void getLatestSensorEvent(int type) {
        Log.d(TAG, "run: in getLatestSensorEvent()");
        CarTestResult carTestResult = new CarTestResult(mCarSensorManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        CarSensorEvent carSensorEvent = null;
        try {
            carSensorEvent = mCarSensorManager.getLatestSensorEvent(type);
            Log.i(TAG, carSensorEvent.toString());
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取最近车速")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(carSensorEvent != null).setTrueValue(carSensorEvent == null ? "null" : carSensorEvent.floatValues[0] + "");
        onOneTestFinished(carTestResult);
    }

    private void isSensorSupported(int type) {
        Log.d(TAG, "run: in isSensorSupported()");
        CarTestResult carTestResult = new CarTestResult(mCarSensorManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean isSupport = false;
        try {
            isSupport = mCarSensorManager.isSensorSupported(CarSensorManager.SENSOR_TYPE_CAR_SPEED);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("是否支持车速传感器")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(isSupport).setTrueValue(isSupport + "");
        onOneTestFinished(carTestResult);
    }

    private void registerListener(CarSensorManager.OnSensorChangedListener sensorChangedListener, int sensorType, int rate ) {
        Log.d(TAG, "run: in registerListener()");
        CarTestResult carTestResult = new CarTestResult(mCarSensorManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean registerResult = false;
        try {
            registerResult = mCarSensorManager.registerListener(sensorChangedListener, sensorType, rate);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("注册传感器监听")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(registerResult).setTrueValue(registerResult + "");
        onOneTestFinished(carTestResult);
    }

    private void unregisterListener(CarSensorManager.OnSensorChangedListener sensorChangedListener, int sensorType) {
        Log.d(TAG, "run: in unregisterListener()");
        CarTestResult carTestResult = new CarTestResult(mCarSensorManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean result = true;
        try{
            mCarSensorManager.registerListener(sensorChangedListener, sensorType,CarSensorManager.SENSOR_RATE_NORMAL );
            mCarSensorManager.unregisterListener(sensorChangedListener, sensorType);
        } catch(Exception e) {
            result = false;
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("取消注册指定传感器监听")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(result);
        onOneTestFinished(carTestResult);
    }

    private void unregisterListener(CarSensorManager.OnSensorChangedListener sensorChangedListener) {
        Log.d(TAG, "run: in unregisterListener()");
        CarTestResult carTestResult = new CarTestResult(mCarSensorManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean result = true;
        try{
            mCarSensorManager.registerListener(sensorChangedListener, CarSensorManager.SENSOR_TYPE_CAR_SPEED,CarSensorManager.SENSOR_RATE_NORMAL );
            mCarSensorManager.unregisterListener(sensorChangedListener);
        } catch(Exception e) {
            result = false;
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("取消注册传感器监听")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(result);
        onOneTestFinished(carTestResult);
    }

    private void getSupportedSensors() {
        Log.d(TAG, "run: in getSupportedSensors()");
        CarTestResult carTestResult = new CarTestResult(mCarSensorManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        List<Integer> supportSensorList = new ArrayList<Integer>();
        try{
            int[] supportSensors = mCarSensorManager.getSupportedSensors();
            for(int i = 0; i< supportSensors.length; i++) {
                supportSensorList.add(supportSensors[i]);
            }
        } catch(Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取支持的传感器")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(supportSensorList.size() > 0).setTrueValue(supportSensorList.toString());
        onOneTestFinished(carTestResult);
    }

    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        Log.d(TAG, "onTestStart: ");
        try {
            mCarSensorManager = (CarSensorManager) mCar.getCarManager(Car.SENSOR_SERVICE);
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

    private CarSensorManager.OnSensorChangedListener mCarSpeedChangedListener = new CarSensorManager.OnSensorChangedListener() {
        @Override
        public void onSensorChanged(CarSensorEvent carSensorEvent) {
            Log.i(TAG, "onSensorChanged: speed is " + carSensorEvent.floatValues[0]);
        }
    };
}
