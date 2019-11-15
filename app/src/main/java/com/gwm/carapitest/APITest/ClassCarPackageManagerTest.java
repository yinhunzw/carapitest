package com.gwm.carapitest.APITest;

import android.car.CarNotConnectedException;
import android.car.content.pm.AppBlockingPackageInfo;
import android.car.content.pm.CarAppBlockingPolicy;
import android.car.content.pm.CarPackageManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.util.Log;

import com.gwm.carapitest.Interfaces.IActivityStateListener;
import com.gwm.carapitest.tool.DefaultActivityStateListener;
import com.gwm.carapitest.tool.TestActivity1;
import com.gwm.carapitest.tool.TestActivity2;
import com.gwm.carapitest.tool.TestActivityBase;

import java.util.List;

public class ClassCarPackageManagerTest extends CarTestBase {
    private CarPackageManager mCarPm;
    private Runnable mRunnableEnsureStartActivity = null;
    private Handler mHandler = new Handler();

    public ClassCarPackageManagerTest(Context context) {
        super(context);
        addRunnable(this::testCarPackageManager);
    }
    private void testCarPackageManager() {
        boolean result = false;
        try {
            testSettingWhitelist();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            onOneTestFinished(new CarTestResult(CarPackageManager.class).setMethod("setAppBlockingPolicy")
                    .setIsSystemApi(true).setResult(result));
            onOneTestFinished(new CarTestResult(CarPackageManager.class).setMethod("isActivityDistractionOptimized").setResult(result));
        }

        result = false;
        try  {
            testIsServiceDistractionOptimized();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            onOneTestFinished(new CarTestResult(CarPackageManager.class).setMethod("isServiceDistractionOptimized").setResult(result));
        }

        testIsActivityBackedBySafeActivity();
    }
    private void assertTrue(boolean t) throws Exception {
        if (!t) throw new Exception();
    }
    private void assertFalse(boolean f) throws Exception {
        assertTrue(!f);
    }

    private void testIsActivityBackedBySafeActivity() {
        final String thisPackage = mContext.getPackageName();
        Signature[] signatures = null;
        try {
            signatures = mContext.getPackageManager().
                    getPackageInfo(thisPackage, PackageManager.GET_SERVICES).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AppBlockingPackageInfo blockingPackageInfo = new AppBlockingPackageInfo(thisPackage,
                0,0,0,signatures, new String[]{TestActivity1.class.getName()});
        CarAppBlockingPolicy carAppBlockingPolicy = new CarAppBlockingPolicy(
                new AppBlockingPackageInfo[]{blockingPackageInfo},null);
        mContext.startActivity(new Intent(mContext,TestActivity1.class));
        mContext.startActivity(new Intent(mContext,TestActivity2.class));
        mRunnableEnsureStartActivity = () -> {
            TestActivityBase testActivity1 = TestActivity1.getInstance();
            TestActivityBase testActivity2 = TestActivity2.getInstance();
            if (testActivity2 != null) {
                testActivity2.registerActivityStateListener(new DefaultActivityStateListener() {
                    @Override
                    public void onResume() {
                        super.onResume();
                        try {
                            mCarPm.setAppBlockingPolicy(thisPackage, carAppBlockingPolicy, CarPackageManager.FLAG_SET_POLICY_WAIT_FOR_CHANGE);
                            CarTestResult carTestResult = new CarTestResult(CarPackageManager.class)
                                    .setMethod("isActivityBackedBySafeActivity")
                                    .setIsSystemApi(true)
                                    .setResult(mCarPm.isActivityBackedBySafeActivity(new ComponentName(thisPackage, TestActivity2.class.getName())));
                            onOneTestFinished(carTestResult);
                            testActivity1.finish();
                            testActivity2.finish();
                            onTestFinished();
                        } catch (CarNotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                mHandler.postDelayed(mRunnableEnsureStartActivity,100);
            }
        };
        mHandler.postDelayed(mRunnableEnsureStartActivity,300);
    }
    private void testIsServiceDistractionOptimized() throws Exception {
        final String carServicePackageName = "com.android.car";
        final String serviceName = "service_allowed";
        final String thisPackage = mContext.getPackageName();
        AppBlockingPackageInfo serviceInfo = new AppBlockingPackageInfo(carServicePackageName,
                0,0,AppBlockingPackageInfo.FLAG_SYSTEM_APP,null,null);
        CarAppBlockingPolicy serviceCarAppBlockingPolicy = new CarAppBlockingPolicy(new AppBlockingPackageInfo[]{serviceInfo},null);


        mCarPm.setAppBlockingPolicy(thisPackage,serviceCarAppBlockingPolicy,CarPackageManager.FLAG_SET_POLICY_WAIT_FOR_CHANGE);
        assertTrue(mCarPm.isServiceDistractionOptimized(carServicePackageName, serviceName));

        mCarPm.setAppBlockingPolicy(thisPackage,serviceCarAppBlockingPolicy,CarPackageManager.FLAG_SET_POLICY_WAIT_FOR_CHANGE|CarPackageManager.FLAG_SET_POLICY_REMOVE);
        assertFalse(mCarPm.isServiceDistractionOptimized(carServicePackageName, serviceName));
    }
    private void testSettingWhitelist() throws Exception {
        final String carServicePackageName = "com.android.car";
        final String activityAllowed = "NO_SUCH_ACTIVITY_BUT_ALLOWED";
        final String activityNotAllowed = "NO_SUCH_ACTIVITY_AND_NOT_ALLOWED";
        final String acticityAllowed2 = "NO_SUCH_ACTIVITY_BUT_ALLOWED2";
        final String thisPackage = mContext.getPackageName();

        AppBlockingPackageInfo info = new AppBlockingPackageInfo(carServicePackageName, 0, 0,
                AppBlockingPackageInfo.FLAG_SYSTEM_APP, null, new String[] { activityAllowed });
        CarAppBlockingPolicy policy = new CarAppBlockingPolicy(new AppBlockingPackageInfo[] { info }
                , null);
        Log.i(TAG, "setting policy");
        mCarPm.setAppBlockingPolicy(thisPackage, policy,
                CarPackageManager.FLAG_SET_POLICY_WAIT_FOR_CHANGE);
        Log.i(TAG, "setting policy done");
        assertTrue(mCarPm.isActivityDistractionOptimized(carServicePackageName, activityAllowed));
        assertFalse(mCarPm.isActivityDistractionOptimized(carServicePackageName,
                activityNotAllowed));

        // replace policy
        info = new AppBlockingPackageInfo(carServicePackageName, 0, 0,
                AppBlockingPackageInfo.FLAG_SYSTEM_APP, null, new String[] { acticityAllowed2 });
        policy = new CarAppBlockingPolicy(new AppBlockingPackageInfo[] { info }
                , null);
        mCarPm.setAppBlockingPolicy(thisPackage, policy,
                CarPackageManager.FLAG_SET_POLICY_WAIT_FOR_CHANGE);
        assertFalse(mCarPm.isActivityDistractionOptimized(carServicePackageName, activityAllowed));
        assertTrue(mCarPm.isActivityDistractionOptimized(carServicePackageName, acticityAllowed2));
        assertFalse(mCarPm.isActivityDistractionOptimized(carServicePackageName,
                activityNotAllowed));

        //add, it replace the whole package policy. So activities are not added.
        info = new AppBlockingPackageInfo(carServicePackageName, 0, 0,
                AppBlockingPackageInfo.FLAG_SYSTEM_APP, null, new String[] { activityAllowed });
        policy = new CarAppBlockingPolicy(new AppBlockingPackageInfo[] { info }
                , null);
        mCarPm.setAppBlockingPolicy(thisPackage, policy,
                CarPackageManager.FLAG_SET_POLICY_WAIT_FOR_CHANGE |
                        CarPackageManager.FLAG_SET_POLICY_ADD);
        assertTrue(mCarPm.isActivityDistractionOptimized(carServicePackageName, activityAllowed));
        assertFalse(mCarPm.isActivityDistractionOptimized(carServicePackageName, acticityAllowed2));
        assertFalse(mCarPm.isActivityDistractionOptimized(carServicePackageName,
                activityNotAllowed));

        //remove
        info = new AppBlockingPackageInfo(carServicePackageName, 0, 0,
                AppBlockingPackageInfo.FLAG_SYSTEM_APP, null, new String[] { activityAllowed });
        policy = new CarAppBlockingPolicy(new AppBlockingPackageInfo[] { info }
                , null);
        mCarPm.setAppBlockingPolicy(thisPackage, policy,
                CarPackageManager.FLAG_SET_POLICY_WAIT_FOR_CHANGE |
                        CarPackageManager.FLAG_SET_POLICY_REMOVE);
        assertFalse(mCarPm.isActivityDistractionOptimized(carServicePackageName, activityAllowed));
        assertFalse(mCarPm.isActivityDistractionOptimized(carServicePackageName, acticityAllowed2));
        assertFalse(mCarPm.isActivityDistractionOptimized(carServicePackageName,
                activityNotAllowed));
    }

    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        for (Runnable r :
                runnableList) {
            r.run();
        }
    }

    @Override
    protected void onTestStop() {

    }
}
