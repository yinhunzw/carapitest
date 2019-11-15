package com.gwm.carapitest.APITest;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.VehicleAreaSeat;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.hvac.CarHvacManager;
import android.content.Context;
import android.hardware.automotive.vehicle.V2_0.VehiclePropertyChangeMode;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.AlertDialogLayout;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ClassCarPropertyConfigTest extends CarTestBase {
    private static final String TAG = ClassCarPropertyConfigTest.class.getSimpleName();
    private CarPropertyConfig<Float> mConfig;
    private CarPropertyConfig<Float> mConfig2;
    private Context mContext;
    private Parcel mParcel;
    protected final static int PROPERTY_ID      = 0xBEEFBEEF;
    protected final static int CAR_AREA_TYPE    = 0xDEADBEEF;
    protected final static int WINDOW_DRIVER    = 0x00000001;
    protected final static int WINDOW_PASSENGER = 0x00000002;
    private ArrayList<Integer> mConfigArray = new ArrayList<Integer>();

    public ClassCarPropertyConfigTest(Context context) {
        super(context);
        mContext = context;
        mConfigArray.add(1);mConfigArray.add(2);mConfigArray.add(3);
        mParcel = Parcel.obtain();
        mConfig = createFloatPropertyConfig();
        mConfig2 = createFloatPropertyConfig2();
        addRunnable(() -> getPropertyId(mConfig));
        addRunnable(() -> getAccess(mConfig));
        addRunnable(() -> getAreaCount(mConfig));
        addRunnable(() -> getAreaType(mConfig));
        addRunnable(() -> getChangeMode(mConfig));
        addRunnable(() -> getMaxValue(mConfig, WINDOW_PASSENGER));
        addRunnable(() -> getFirstAndOnlyAreaId(mConfig2));
        addRunnable(() -> getMaxValue(mConfig2));
        addRunnable(() -> getMinValue(mConfig2));
        addRunnable(() -> getMaxSampleRate(mConfig2));
        addRunnable(() -> getMinSampleRate(mConfig2));
        addRunnable(() -> describeContents(mConfig));
        addRunnable(() -> isGlobalProperty(mConfig));
        addRunnable(() -> toString(mConfig));
        addRunnable(() -> getConfigArray(mConfig));
        addRunnable(() -> hasArea(mConfig, WINDOW_PASSENGER));
    }

    private CarPropertyConfig<Float> createFloatPropertyConfig2() {
        CarPropertyConfig<Float> config = CarPropertyConfig
                .newBuilder(Float.class, PROPERTY_ID, CAR_AREA_TYPE)
                .addAreaConfig(WINDOW_DRIVER, 1f, 20.0f).setMaxSampleRate(3.0f).setMinSampleRate(1.0f).setChangeMode(VehiclePropertyChangeMode.ON_CHANGE)
                .setConfigString("gwm configString").setConfigArray(mConfigArray)
                .build();
        return config;
    }

    private CarPropertyConfig<Float> createFloatPropertyConfig() {
        CarPropertyConfig<Float> config = CarPropertyConfig
                .newBuilder(Float.class, PROPERTY_ID, CAR_AREA_TYPE)
                .addArea(WINDOW_DRIVER)
                .addAreaConfig(WINDOW_PASSENGER, 10f, 20f)
                .build();
        return config;
    }

    private void getPropertyId(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getPropertyId()");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        int value = config.getPropertyId();
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true)
                .setExpectedValue(PROPERTY_ID +  "").setTrueValue(value + "").setResult(PROPERTY_ID  == value);
        onOneTestFinished(carTestResult);
    }

    private void describeContents(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getPropertyId()");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        int value = config.describeContents();
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true)
                .setTrueValue(value + "").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getAccess(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getAccess()");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        int value = config.getAccess();
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true)
                .setTrueValue(value + "").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getAreaCount(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getAreaCount()");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        int value = config.getAreaCount();
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true)
                .setTrueValue(value + "").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getAreaType(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getAreaType()");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        int value = config.getAreaType();
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true)
                .setTrueValue(value + "").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getFirstAndOnlyAreaId(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getFirstAndOnlyAreaId()");
        StringBuilder exceptionDescription = new StringBuilder("");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        int value = 0;
        try {
            value = config.getFirstAndOnlyAreaId();
        }catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value + "").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getMaxValue(CarPropertyConfig<Float> config, int areaId) {
        Log.d(TAG, "run: in getMaxValue()");
        StringBuilder exceptionDescription = new StringBuilder("");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        float value = 0;
        try {
            value = config.getMaxValue(areaId);
            Log.i(TAG, value +"");
        }catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value + "").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getMaxValue(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getMaxValue()");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        float value = config.getMinValue();
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true)
                .setTrueValue(value + "").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getMinValue(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getMinValue()");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        float value = config.getMinValue();
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true)
                .setTrueValue(value + "").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void isGlobalProperty(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in isGlobalProperty()");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        boolean value = config.isGlobalProperty();
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true)
                .setTrueValue(value + "").setResult(true);
        onOneTestFinished(carTestResult);
    }

    private void getMaxSampleRate(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getMaxSampleRate()");
        StringBuilder exceptionDescription = new StringBuilder("");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        float value = 0;
        try {
            value = config.getMaxSampleRate();
            Log.i(TAG, value +"");
        }catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value + "").setResult(true).setExpectedValue("3.0");
        onOneTestFinished(carTestResult);
    }

    private void getMinSampleRate(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getMinSampleRate()");
        StringBuilder exceptionDescription = new StringBuilder("");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        float value = 0;
        try {
            value = config.getMinSampleRate();
            Log.i(TAG, value +"");
        }catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value + "").setResult(value == 1.0).setExpectedValue("1.0");
        onOneTestFinished(carTestResult);
    }

    private void getChangeMode(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getMinSampleRate()");
        StringBuilder exceptionDescription = new StringBuilder("");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        int value = 0;
        try {
            value = config.getChangeMode();
            Log.i(TAG, value +"");
        }catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value + "").setResult(VehiclePropertyChangeMode.ON_CHANGE == value).setExpectedValue(VehiclePropertyChangeMode.ON_CHANGE + "");
        onOneTestFinished(carTestResult);
    }

    private void hasArea(CarPropertyConfig<Float> config, int areaId) {
        Log.d(TAG, "run: in hasArea()");
        StringBuilder exceptionDescription = new StringBuilder("");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        boolean value = false;
        try {
            value = config.hasArea(areaId);
            Log.i(TAG, value +"");
        }catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value + "").setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    private void getConfigArray(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in getConfigArray()");
        StringBuilder exceptionDescription = new StringBuilder("");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        List<Integer> configArray = new ArrayList<>();
        try {
            configArray = config.getConfigArray();
        }catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(configArray + "").setExpectedValue(mConfigArray.toString()).setResult(mConfigArray.size() == configArray.size() && mConfigArray.containsAll(configArray));
        onOneTestFinished(carTestResult);
    }

    private void toString(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in hasArea()");
        StringBuilder exceptionDescription = new StringBuilder("");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        String value = "";
        try {
            value = config.toString();
            Log.i(TAG, value +"");
        }catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value + "").setResult("".equals(exceptionDescription.toString()));
        onOneTestFinished(carTestResult);
    }

    private void getConfigString(CarPropertyConfig<Float> config) {
        Log.d(TAG, "run: in hasArea()");
        StringBuilder exceptionDescription = new StringBuilder("");
        CarTestResult carTestResult = new CarTestResult(config.getClass());
        String value = "";
        try {
            value = config.getConfigString();
            Log.i(TAG, value +"");
        }catch (Exception e) {
            exceptionDescription.append(e.toString());
        }
        carTestResult
                .setMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).setIsSystemApi(true).setException(exceptionDescription.toString())
                .setTrueValue(value + "").setExpectedValue("gwm configString").setResult("gwm configString".equals(value));
        onOneTestFinished(carTestResult);
    }


    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        Log.d(TAG, "onTestStart: ");
        for (Runnable run:runnableList) {
            run.run();
        }
        onTestFinished();
    }

    @Override
    protected void onTestStop() {
        Log.d(TAG, "onTestStop: ");
    }

    protected  <T extends Parcelable> T readFromParcel() {
        mParcel.setDataPosition(0);
        return mParcel.readParcelable(null);
    }

    protected void writeToParcel(Parcelable value) {
        mParcel.writeParcelable(value, 0);
    }
}
