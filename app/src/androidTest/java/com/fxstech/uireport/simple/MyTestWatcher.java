package com.fxstech.uireport.simple;

import android.util.Log;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class MyTestWatcher extends TestWatcher {
    private String TAG="MyTestWatcher";
    @Override
    protected void succeeded(Description description) {
        Log.v(TAG,description.getMethodName()+":成功了");
    }
    @Override
    protected void failed(Throwable e, Description description) {
        Log.v(TAG,description.getMethodName()+":失败了");
    }
    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        Log.v(TAG,description.getMethodName()+":被忽略了");
    }
    @Override
    protected void starting(Description description) {
        Log.v(TAG,description.getMethodName()+":开始了");
    }
    @Override
    protected void finished(Description description) {
        Log.v(TAG,description.getMethodName()+":结束了");
    }
}
