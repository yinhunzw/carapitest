package com.gwm.carapitest.APITest;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public class CarTestResult {
    public final static int MODULE_TEST_INVALID= 0x0;
    public final static int MODULE_TEST_FINISHED = 0x1;
    public final static int MODULE_TEST_STOPPED = 0x2;
    public final static int MODULE_TEST_ERROR_CAR_NOT_CONNECTED = 0x3;
    private String mPath = "";
    private String mPackage = "";
    private String mClassName = "";
    private String mMethod = "";
    private String mFeature = "";// A whole function test.
    private String mExpectedValue = "";
    private String mTrueValue = "";
    private String mException = "";
    private String mComment = "";
    private String mStackTrace = "";
    private boolean mResult;
    private boolean mIsSystemApi;

    private int mModuleTestStatus;
    @IntDef ({MODULE_TEST_INVALID,MODULE_TEST_FINISHED,MODULE_TEST_STOPPED,MODULE_TEST_ERROR_CAR_NOT_CONNECTED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ModuleTestStatus {}

    public CarTestResult() {}
    public CarTestResult(Class c) {
        mPackage = c.getPackage().getName();
        mPath = c.getName();
        mClassName = c.getSimpleName();
    }

    @Override
    public String toString() {
        return "CarTestResult{" +
                "mPath='" + mPath + '\'' +
                ", mPackage='" + mPackage + '\'' +
                ", mClassName='" + mClassName + '\'' +
                ", mMethod='" + mMethod + '\'' +
                ", mFeature='" + mFeature + '\'' +
                ", mExpectedValue='" + mExpectedValue + '\'' +
                ", mTrueValue='" + mTrueValue + '\'' +
                ", mException='" + mException + '\'' +
                ", mComment='" + mComment + '\'' +
                ", mStackTrace='" + mStackTrace + '\'' +
                ", mResult=" + mResult +
                ", mIsSystemApi=" + mIsSystemApi +
                ", mModuleTestStatus=" + mModuleTestStatus +
                '}';
    }

    public String getPath() {
        return mPath;
    }

    public CarTestResult setPath(String mPath) {
        this.mPath = mPath;
        return this;
    }

    public String getPackage() {
        return mPackage;
    }

    public CarTestResult setPackage(String mPackage) {
        this.mPackage = mPackage;
        return this;
    }

    public String getClassName() {
        return mClassName;
    }

    public CarTestResult setClassName(String mClassName) {
        this.mClassName = mClassName;
        return this;
    }

    public String getMethod() {
        return mMethod;
    }

    public CarTestResult setMethod(String mMethod) {
        this.mMethod = mMethod;
        return this;
    }

    public String getFeature() {
        return mFeature;
    }

    public CarTestResult setFeature(String mFeature) {
        this.mFeature = mFeature;
        return this;
    }

    public String getExpectedValue() {
        return mExpectedValue;
    }

    public CarTestResult setExpectedValue(String mExpectedValue) {
        this.mExpectedValue = mExpectedValue;
        return this;
    }

    public String getTrueValue() {
        return mTrueValue;
    }

    public CarTestResult setTrueValue(String mTrueValue) {
        this.mTrueValue = mTrueValue;
        return this;
    }

    public String getException() {
        return mException;
    }

    public CarTestResult setException(String mException) {
        this.mException = mException;
        return this;
    }

    public String getComment() {
        return mComment;
    }

    public CarTestResult setComment(String mLog) {
        this.mComment = mLog;
        return this;
    }

    public String getStackTrace() {
        return this.mStackTrace;
    }
    public boolean isPassed() {
        return mResult;
    }

    public CarTestResult setResult(boolean pass) {
        this.mResult = pass;
        if (!this.mResult) {
             Arrays.asList(new Exception("Failure Path").getStackTrace()).forEach(e->mStackTrace += e.toString());
        }
        return this;
    }

    public boolean isSystemApi() {
        return mIsSystemApi;
    }

    public CarTestResult setIsSystemApi(boolean mIsSystemApi) {
        this.mIsSystemApi = mIsSystemApi;
        return this;
    }

    public int getModuleTestStatus() {
        return mModuleTestStatus;
    }

    public CarTestResult setModuleTestStatus(@ModuleTestStatus int mModuleTestStatus) {
        this.mModuleTestStatus = mModuleTestStatus;
        return this;
    }
}
