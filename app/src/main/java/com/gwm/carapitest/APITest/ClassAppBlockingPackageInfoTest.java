package com.gwm.carapitest.APITest;

import android.car.content.pm.AppBlockingPackageInfo;
import android.car.content.pm.CarAppBlockingPolicy;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Parcel;
import android.util.Log;

import java.util.List;

public class ClassAppBlockingPackageInfoTest extends CarTestBase {
    private static final String TAG = ClassAppBlockingPackageInfoTest.class.getSimpleName();
    private static final String featureOfSystemInfo = "ParcellingAppBlockingPackageInfoOfSystemPackage,对系统app的AppBlockingPackageInfo进行序列化";
    private static final String featureOfNonSystemInfo = "ParcellingAppBlockingPackageInfoOfSystemPackage,对非系统app的AppBlockingPackageInfo进行序列化";
    private int mNumTestFinished = 0;
    private int mTestCnt = 0;

    public ClassAppBlockingPackageInfoTest(Context context) {
        super(context);
        addRunnable(()->{testParcellingNonSystemInfo();testCount();});
        addRunnable(()->{testParcellingSystemInfo();testCount();});
    }

    public void testParcellingSystemInfo(){
        AppBlockingPackageInfo carServiceInfo = createInfoCarService(mContext);
        Parcel dest = Parcel.obtain();
        carServiceInfo.writeToParcel(dest, 0);
        dest.setDataPosition(0);
        AppBlockingPackageInfo carServiceInfoRead = new AppBlockingPackageInfo(dest);
        Log.i(TAG, "expected:" + carServiceInfo + ",read:" + carServiceInfoRead);
        onOneTestFinished(new CarTestResult(AppBlockingPackageInfo.class)
                .setResult(carServiceInfo.equals(carServiceInfoRead))
                .setFeature(featureOfSystemInfo.split(",")[1]));
    }

    public void testParcellingNonSystemInfo(){
        AppBlockingPackageInfo selfInfo = createInfoSelf(mContext);
        Parcel dest = Parcel.obtain();
        selfInfo.writeToParcel(dest, 0);
        dest.setDataPosition(0);
        AppBlockingPackageInfo selfInfoRead = new AppBlockingPackageInfo(dest);
        Log.i(TAG, "expected:" + selfInfo + ",read:" + selfInfoRead);
        onOneTestFinished(new CarTestResult(AppBlockingPackageInfo.class)
                .setResult(selfInfo.equals(selfInfoRead))
                .setFeature(featureOfNonSystemInfo.split(",")[1]));
    }

    public static AppBlockingPackageInfo createInfoCarService(Context context) {
        final String packageName = "android.car";
        return new AppBlockingPackageInfo(packageName, 0, 0, AppBlockingPackageInfo.FLAG_SYSTEM_APP,
                null, null);
    }

    public static final AppBlockingPackageInfo createInfoSelf(Context context) {
        final String packageName = "com.gwm.carapitest";
        PackageManager pm = context.getPackageManager();
        Signature[] signatures;
        try {
            signatures = pm.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        String[] activties = new String[] { "Hello", "World" };
        return new AppBlockingPackageInfo(packageName, 0, 100, 0, signatures, activties);
    }

    private void testCount() {
        mNumTestFinished ++;
        if(++ mNumTestFinished == mTestCnt) {
            onTestFinished();
        }
    }
    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        mTestCnt = runnableList.size();
        for (Runnable r :
                runnableList) {
            new Thread(r).start();
        }
    }

    @Override
    protected void onTestStop() {

    }
}
