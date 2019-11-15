package com.gwm.carapitest.APITest;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.VehicleAreaSeat;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.CarHvacManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class ClassCarHvacManagerTest extends CarTestBase {
    private static final String TAG = ClassCarHvacManagerTest.class.getSimpleName();
    private CarHvacManager mCarHvacManager;
    private Context mContext;
    public static final int DRIVER_ZONE_ID = VehicleAreaSeat.SEAT_ROW_1_LEFT |
            VehicleAreaSeat.SEAT_ROW_2_LEFT | VehicleAreaSeat.SEAT_ROW_2_CENTER;
    public static final int PASSENGER_ZONE_ID = VehicleAreaSeat.SEAT_ROW_1_RIGHT |
            VehicleAreaSeat.SEAT_ROW_2_RIGHT;
    public static final int SEAT_ALL = VehicleAreaSeat.SEAT_ROW_1_LEFT |
            VehicleAreaSeat.SEAT_ROW_1_RIGHT | VehicleAreaSeat.SEAT_ROW_2_LEFT |
            VehicleAreaSeat.SEAT_ROW_2_CENTER | VehicleAreaSeat.SEAT_ROW_2_RIGHT;

    public ClassCarHvacManagerTest(Context context) {
        super(context);
        mContext = context;
        addRunnable(() -> setIntProperty(CarHvacManager.ID_ZONED_FAN_SPEED_SETPOINT, SEAT_ALL, 2));
        addRunnable(() -> getIntProperty(CarHvacManager.ID_ZONED_FAN_SPEED_SETPOINT, SEAT_ALL));
        addRunnable(() -> setBooleanProperty(CarHvacManager.ID_ZONED_AC_ON, SEAT_ALL, true));
        addRunnable(() -> getBooleanProperty(CarHvacManager.ID_ZONED_AC_ON, SEAT_ALL));
        addRunnable(() -> setFloatProperty(CarHvacManager.ID_ZONED_TEMP_SETPOINT, DRIVER_ZONE_ID, 28f));
        addRunnable(() -> getFloatProperty(CarHvacManager.ID_ZONED_TEMP_SETPOINT, DRIVER_ZONE_ID));
        addRunnable(() -> isPropertyAvailable(CarHvacManager.ID_ZONED_TEMP_SETPOINT, DRIVER_ZONE_ID));
        addRunnable(() -> registerCallback(mHardwareCallback));
        addRunnable(() -> unregisterCallback(mHardwareCallback));
    }

    private void getIntProperty(int propertyId, int area) {
        Log.d(TAG, "run: in getBooleanProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarHvacManager.getClass());
        String result = "";
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            result = mCarHvacManager.getIntProperty(propertyId, area) + "";
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取空调风速")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setExpectedValue("2").setTrueValue(result).setResult("2".equals(result));
        onOneTestFinished(carTestResult);
    }

    private void setIntProperty(int propertyId, int area, int value) {
        Log.d(TAG, "run: in setBooleanProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarHvacManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCarHvacManager.setIntProperty(propertyId, area, value);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("设置空调风速")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setExpectedValue("2").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getBooleanProperty(int propertyId, int area) {
        Log.d(TAG, "run: in setBooleanProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarHvacManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean value = false;
        try {
            value = mCarHvacManager.getBooleanProperty(propertyId, area);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取空调AC 状态")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value+ "").setExpectedValue("true").setResult(value);
        onOneTestFinished(carTestResult);
    }

    private void setBooleanProperty(int propertyId, int area, boolean value) {
        Log.d(TAG, "run: in setBooleanProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarHvacManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCarHvacManager.setBooleanProperty(propertyId, area, value);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("设置空调AC ON")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void setFloatProperty(int propertyId, int area, float value) {
        Log.d(TAG, "run: in setBooleanProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarHvacManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCarHvacManager.setFloatProperty(propertyId, area, value);
        } catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("设置左侧温度")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getFloatProperty(int propertyId, int area) {
        Log.d(TAG, "run: in getFloatProperty()");
        CarTestResult carTestResult = new CarTestResult(mCarHvacManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        float value = 0f;
        try {
            value = mCarHvacManager.getFloatProperty(propertyId, area);
        } catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("获取左侧温度")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value+ "").setExpectedValue("28.0").setResult("28.0".equals(value + ""));
        onOneTestFinished(carTestResult);
    }

    private void isPropertyAvailable(int propertyId, int area) {
        Log.d(TAG, "run: in isPropertyAvailable()");
        CarTestResult carTestResult = new CarTestResult(mCarHvacManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        boolean value = false;
        try {
            value = mCarHvacManager.isPropertyAvailable(propertyId, area);
        } catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("是否支持指定区域的属性：左侧温度\n sss")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value+ "").setExpectedValue("true").setResult(value);
        onOneTestFinished(carTestResult);
    }

    private void registerCallback(CarHvacManager.CarHvacEventCallback hardwareCallback) {
        Log.d(TAG, "run: in registerCallback()");
        CarTestResult carTestResult = new CarTestResult(mCarHvacManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        try {
            mCarHvacManager.registerCallback(hardwareCallback);
        } catch (CarNotConnectedException e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult.setFeature("注册监听回调")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(exceptionDescription.length() == 0);
        onOneTestFinished(carTestResult);
    }

    private void unregisterCallback(CarHvacManager.CarHvacEventCallback hardwareCallback) {
        Log.d(TAG, "run: in unregisterCallback()");
        CarTestResult carTestResult = new CarTestResult(mCarHvacManager.getClass());
        StringBuilder exceptionDescription = new StringBuilder("");
        mCarHvacManager.unregisterCallback(hardwareCallback);
        carTestResult.setFeature("取消注册监听回调")
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setResult(exceptionDescription.length() == 0);
        onOneTestFinished(carTestResult);
    }

    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        Log.d(TAG, "onTestStart: ");
        try {
            mCarHvacManager = (CarHvacManager) mCar.getCarManager(Car.HVAC_SERVICE);
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

    private final CarHvacManager.CarHvacEventCallback mHardwareCallback = new CarHvacManager.CarHvacEventCallback() {
        @Override
        public void onChangeEvent(final CarPropertyValue val) {
            try {
                int areaId = val.getAreaId();
                switch (val.getPropertyId()) {
                    case CarHvacManager.ID_ZONED_AC_ON:
                        boolean acState = getValue(val);
                        Log.i(TAG, "onChangeEvent: " + getValue(val));
                        break;
                    case CarHvacManager.ID_ZONED_FAN_DIRECTION:
                        Log.i(TAG, "onChangeEvent: FanPosition: " + getValue(val));
                        break;
                    case CarHvacManager.ID_ZONED_FAN_SPEED_SETPOINT:
                        Log.i(TAG, "onChangeEvent: " + getValue(val));
                        break;
                    case CarHvacManager.ID_ZONED_TEMP_SETPOINT:
                        Log.i(TAG, "onChangeEvent: " + areaId + " " + getValue(val));
                        break;
                    case CarHvacManager.ID_ZONED_HVAC_POWER_ON:
                        Log.i(TAG, "onChangeEvent: " + getValue(val));
                        break;
                    default:
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "Unhandled HVAC event, id: " + val.getPropertyId());
                        }
                }
            } catch (Exception e) {
                // catch all so we don't take down the sysui if a new data type is
                // introduced.
                Log.e(TAG, "Failed handling hvac change event", e);
            }
        }

        @Override
        public void onErrorEvent(final int propertyId, final int zone) {
            Log.d(TAG, "HVAC error event, propertyId: " + propertyId +
                    " zone: " + zone);
        }
    };

    public static <E> E getValue(CarPropertyValue propertyValue) {
        return (E) propertyValue.getValue();
    }
}
