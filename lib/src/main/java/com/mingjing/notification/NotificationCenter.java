package com.mingjing.notification;

import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liukui on 2017/4/25.
 */

public enum NotificationCenter {
    INSTANCE;

    Handler mMainHandler = new Handler(Looper.getMainLooper());
    long mMainThreadId = Looper.getMainLooper().getThread().getId();

    Map<Object, Boolean> mObservers = new ConcurrentHashMap<>();
    Map<Class<?>, Notification> mNotifications = new HashMap<>();

    public void addObserver(final Object obj) {
        if (isMainThread()) {
            mObservers.put(obj, true);
        } else {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mObservers.put(obj, true);
                }
            });
        }
    }

    public <T> T getObserver(Class<T> cls) {

        Notification<T> notification = mNotifications.get(cls);
        if (notification == null) {
            notification = new Notification(cls, mMainHandler, mObservers);
            mNotifications.put(cls, notification);
        }

        T t = notification.getProxy();

        if (t == null) {
            Log.e("NotificationCenter", "there is no such an observer, check it if add annotation");
        }
        return t;
    }

    public void removeObserver(final Object obj) {
        if (isMainThread()) {
            mObservers.remove(obj);
        } else {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mObservers.remove(obj);
                }
            });
        }
    }

    public void removeAll() {
        if (isMainThread()) {
            mObservers.clear();
        } else {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mObservers.clear();
                }
            });
        }
    }

    private boolean isMainThread() {
        return Thread.currentThread().getId() == mMainThreadId;
    }
}
