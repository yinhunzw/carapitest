package com.gwm.carapitest.Interfaces;

import com.gwm.carapitest.APITest.CarTestResult;

import java.util.List;

public interface IListenTest {
    void onFinished(List<CarTestResult> mResultList);
}
