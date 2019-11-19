package com.fxstech.uireport.perfect;

import android.app.Instrumentation;
import android.os.RemoteException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

@RunWith(AndroidJUnit4.class)
public class UI2Demo {
    private static Instrumentation mInstrumentation;
    private static UiDevice mUiDevice;
    @Rule
    public ReportWatcher eu = new ReportWatcher(mInstrumentation);

    @BeforeClass
    public static void beforeClass() {
        mInstrumentation= InstrumentationRegistry.getInstrumentation();
        mUiDevice=UiDevice.getInstance(mInstrumentation);
    }

    @TestDescription(value = "用例示例1")
    @Test
    public void testDemo1() throws RemoteException {
        mUiDevice.pressRecentApps();
    }

    @TestDescription(value = "用例示例2")
    @Test
    public void testDemo2() {
        Assert.assertTrue(false);
    }

    @TestDescription(value = "用例示例3")
    @Test
    public void testDemo3() throws RemoteException {
        mUiDevice.pressRecentApps();
    }

    @TestDescription(value = "用例示例4")
    @Test
    public void testDemo4() throws RemoteException {
        mUiDevice.pressRecentApps();
    }

}
