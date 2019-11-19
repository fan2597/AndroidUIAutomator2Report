package com.fxstech.uireport.simple;

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
public class TestDemo {
    private static Instrumentation mInstrumentation;
    private static UiDevice mUiDevice;
    @Rule
    public MyTestWatcher testWatcher=new MyTestWatcher();
    @BeforeClass
    public static void beforeClass() {
        mInstrumentation= InstrumentationRegistry.getInstrumentation();
        mUiDevice=UiDevice.getInstance(mInstrumentation);
    }
    @Test
    public void testDemo1() throws RemoteException {
        mUiDevice.pressRecentApps();
    }
    @Test
    public void testDemo2() throws RemoteException {
        mUiDevice.pressRecentApps();
    }
    @Test
    public void testDemo3() {
        Assert.assertTrue(false);
    }
    @Test
    public void testDemo4() throws RemoteException {
        mUiDevice.pressRecentApps();
    }
}
