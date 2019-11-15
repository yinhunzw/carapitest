package com.gwm.carapitest.APITest;

import android.car.Car;
import android.car.CarInfoManager;
import android.car.CarNotConnectedException;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class ClassCarInfoManagerTest extends CarTestBase {
    private static final String TAG = "ClassCarInfoManagerTest";

    public CarInfoManager mCarInfoManager;

    public ClassCarInfoManagerTest(Context context) {
        super(context);

        addRunnable(() -> testGetVehicleId());
        addRunnable(() -> testGetManufacturer());
        addRunnable(() -> testGetModel());
        addRunnable(() -> testGetModelYear());
        addRunnable(() -> testGetFuelCapacity());
        addRunnable(() -> testGetEvBatteryCapacity());
        addRunnable(() -> testGetFuelTypes());
        addRunnable(() -> testGetEvConnectorTypes());
    }


    private void testGetVehicleId() {
        Log.d(TAG, "testGetVehicleId: ");
        CarTestResult ctr = new CarTestResult(CarInfoManager.class);
        ctr.setMethod("getVehicleId").setIsSystemApi(false).setFeature("获取车架号");
        try {
            String vin = mCarInfoManager.getVehicleId();
            if (vin != null) {
                if(vin.isEmpty()){
                    ctr.setResult(true).setTrueValue("VIN is empty, maybe no initial Value in HAL");
                } else {
                    ctr.setResult(true).setTrueValue(vin);
                }
            } else {
                ctr.setResult(false).setComment("maybe not config VehicleId in HAL, please check it!");
            }
            onOneTestFinished(ctr);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
            ctr.setResult(false).setException(e.toString());
            onOneTestFinished(ctr);
        }
    }

    private void testGetManufacturer() {

        Log.d(TAG, "testGetManufacturer: ");
        CarTestResult ctr = new CarTestResult(CarInfoManager.class);
        ctr.setMethod("getManufacturer").setIsSystemApi(false).setFeature("获取厂商信息");
        try {
            String infoMake = mCarInfoManager.getManufacturer();
            // the value of INFO_MAKE is "Toy Vehicle" in AS Emulator.
            // but others' hasn't been initialized.
            if (infoMake != null) {
                ctr.setResult(true).setTrueValue(infoMake);
            } else {
                ctr.setResult(false).setComment("maybe not config Manufacturer in HAL, please check it!");
            }
            onOneTestFinished(ctr);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
            ctr.setResult(false).setException(e.toString());
            onOneTestFinished(ctr);
        }

    }

    private void testGetModel() {

        Log.d(TAG, "testGetModel ");
        CarTestResult ctr = new CarTestResult(CarInfoManager.class);
        ctr.setMethod("getModel").setIsSystemApi(false).setFeature("获取车辆型号");
        try {
            String model = mCarInfoManager.getModel();
            Log.d(TAG, "testGetModel: model ="+model);
            if (model != null) {
                ctr.setResult(true).setTrueValue(model);
            } else {
                ctr.setResult(false).setComment("maybe not config model in HAL, please check it!");
            }
            onOneTestFinished(ctr);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
            ctr.setResult(false).setException(e.toString());
            onOneTestFinished(ctr);
        }

    }

    private void testGetModelYear() {

        Log.d(TAG, "testGetModelYear ");
        CarTestResult ctr = new CarTestResult(CarInfoManager.class);
        ctr.setMethod("getModelYear").setIsSystemApi(false).setFeature("获取车辆年份");
        try {
            String modelYear = mCarInfoManager.getModelYear();
            Log.d(TAG, "testGetModel: modelYear ="+modelYear);
            if (modelYear != null) {
                ctr.setResult(true).setTrueValue(modelYear);
            } else {
                ctr.setResult(false).setComment("Maybe no config modelYear in HAL, please check it~");
            }
            onOneTestFinished(ctr);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
            ctr.setResult(false).setException(e.toString());
            onOneTestFinished(ctr);
        }

    }

    private void testGetFuelCapacity() {

        Log.d(TAG, "testGetFuelCapacity ");
        CarTestResult ctr = new CarTestResult(CarInfoManager.class);
        ctr.setMethod("getFuelCapacity").setIsSystemApi(false).setFeature("获取油箱容量");
        try {
            float fuelCapacity = mCarInfoManager.getFuelCapacity();
            if (fuelCapacity > 0f) {
                ctr.setResult(true).setTrueValue(Float.toString(fuelCapacity));
            } else {
                ctr.setResult(false).setComment("Maybe no config FuelCapacity in HAL, please check it~");
            }
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
            ctr.setResult(false).setException(e.toString());
            onOneTestFinished(ctr);
        }

    }

    private void testGetEvBatteryCapacity() {

        Log.d(TAG, "testGetEvBatteryCapacity ");
        CarTestResult ctr = new CarTestResult(CarInfoManager.class);
        ctr.setMethod("getEvBatteryCapacity").setIsSystemApi(false).setFeature("获取电池容量");
        try {
            float evBatteryCapacity = mCarInfoManager.getEvBatteryCapacity();
            if (evBatteryCapacity > 0f) {
                ctr.setResult(true).setTrueValue(Float.toString(evBatteryCapacity));
            } else {
                ctr.setResult(false).setComment("Maybe no config EvBatteryCapacity in HAL, please check it~");
            }
            onOneTestFinished(ctr);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
            ctr.setResult(false).setException(e.toString());
            onOneTestFinished(ctr);
        }

    }

    private void testGetFuelTypes() {

        Log.d(TAG, "testGetFuelTypes ");
        CarTestResult ctr = new CarTestResult(CarInfoManager.class);
        ctr.setMethod("getFuelTypes").setIsSystemApi(false).setFeature("获取燃料类型");
        try {
            int[] fuelTypes = mCarInfoManager.getFuelTypes();
            int length = fuelTypes.length;
            Log.d(TAG, " length = " + length);

            if (fuelTypes.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int fuel : fuelTypes) {
                    sb.append(Integer.toString(fuel) + ",");
                }

                ctr.setResult(true).setTrueValue(sb.toString());
            } else {
                ctr.setResult(false).setComment("Maybe no config FuelTypes in HAL, please check it~");
            }
            onOneTestFinished(ctr);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
            ctr.setResult(false).setException(e.toString());
            onOneTestFinished(ctr);
        }

    }

    private void testGetEvConnectorTypes() {

        Log.d(TAG, "testGetEvConnectorTypes ");
        CarTestResult ctr = new CarTestResult(CarInfoManager.class);
        ctr.setMethod("getEvConnectorTypes").setIsSystemApi(false).setFeature("获取充电连接类型");

        try {
            int[] evConnectorTypes = mCarInfoManager.getEvConnectorTypes();
            int length = evConnectorTypes.length;

            if (length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int connector : evConnectorTypes) {
                    sb.append(Integer.toString(connector) + ",");
                }
                ctr.setResult(true).setTrueValue(sb.toString());
            } else {
                ctr.setResult(false).setComment("Maybe no config EvConnectorTypes in HAL, please check it~");
            }
            onOneTestFinished(ctr);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
            ctr.setResult(false).setException(e.toString());
            onOneTestFinished(ctr);
        }

    }

    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        Log.d(TAG, "onTestStart: ");

        try {
            mCarInfoManager = (CarInfoManager) mCar.getCarManager(Car.INFO_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Runnable run : runnableList) {
            run.run();
        }
        onTestFinished();
    }

    @Override
    protected void onTestStop() {
        Log.d(TAG, "onTestStop: ");
        //mCar.disconnect();
    }
}
