package com.mingjing.notification;

import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Created by liukui on 2017/4/25.
 */

class Notification<T> {


    private Class<T> mCallbackCls;
    private Handler mMainHandler;
    private final Map<Object, Boolean> mObservers;
    private T mProxy;

    Notification(Class<T> cls, Handler mainHandler, Map<Object, Boolean> observers) {
        mCallbackCls = cls;
        mMainHandler = mainHandler;
        mObservers = observers;
    }

    public T getProxy() {
        if (mProxy == null) {
            try {
                String clsName = "com.mingjing.notification.proxy." +mCallbackCls.getSimpleName() + "_Proxy";
                Constructor constructor = Class.forName(clsName).getConstructor(Map.class);
                mProxy = (T) constructor.newInstance(mObservers);
            } catch (Exception e) {
                Log.e("notification", e.toString());
                e.printStackTrace();
            }
        }
        return mProxy;
    }
}
