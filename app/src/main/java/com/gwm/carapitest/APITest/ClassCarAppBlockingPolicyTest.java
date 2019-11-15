package com.gwm.carapitest.APITest;

import android.car.content.pm.AppBlockingPackageInfo;
import android.car.content.pm.CarAppBlockingPolicy;
import android.content.Context;
import android.os.Parcel;
import android.util.Log;

import java.util.List;

public class ClassCarAppBlockingPolicyTest extends CarTestBase {
    private static final String TAG = ClassCarAppBlockingPolicyTest.class.getSimpleName();
    private static final String featureOfrAppBlockingPolicy = "Parcelling CarAppBlockingPolicy,对CarAppBlockingPolicy进行序列化";

    public ClassCarAppBlockingPolicyTest(Context context) {
        super(context);
        addRunnable(this::testParcelling);
    }

    public void testParcelling() {
        AppBlockingPackageInfo carServiceInfo =
                ClassAppBlockingPackageInfoTest.createInfoCarService(mContext);
        AppBlockingPackageInfo selfInfo =
                ClassAppBlockingPackageInfoTest.createInfoSelf(mContext);
        // this is only for testing parcelling. contents has nothing to do with actual app blocking.
        AppBlockingPackageInfo[] whitelists = new AppBlockingPackageInfo[] { carServiceInfo,
                selfInfo };
        AppBlockingPackageInfo[] blacklists = new AppBlockingPackageInfo[] { selfInfo };
        CarAppBlockingPolicy policyExpected = new CarAppBlockingPolicy(whitelists, blacklists);
        Parcel dest = Parcel.obtain();
        policyExpected.writeToParcel(dest, 0);
        dest.setDataPosition(0);
        CarAppBlockingPolicy policyRead = new CarAppBlockingPolicy(dest);
        onOneTestFinished(new CarTestResult(CarAppBlockingPolicy.class)
                .setResult(policyExpected.equals(policyRead))
                .setMethod(featureOfrAppBlockingPolicy.split(",")[1]));
        Log.i(TAG, "expected:" + policyExpected + ",read:" + policyRead);
    }
    @Override
    protected void onTestStart(List<Runnable> runnableList) {
        runnableList.forEach(r->r.run());
        onTestFinished();
    }

    @Override
    protected void onTestStop() {

    }
}