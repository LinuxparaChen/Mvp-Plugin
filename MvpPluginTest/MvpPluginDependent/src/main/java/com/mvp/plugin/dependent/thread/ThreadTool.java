package com.mvp.plugin.dependent.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTool {

    private static Handler MAIN_HNDLER;
    private static ExecutorService ASYNC_THREAD_POOL;

    public static final void executeOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            getMainHandler().post(runnable);
        }
    }

    public static final void executeOnAsyncThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            getAsyncThreadPool().execute(runnable);
        } else {
            runnable.run();
        }
    }

    private static Handler getMainHandler() {
        if (MAIN_HNDLER == null) {
            synchronized (ThreadTool.class) {
                if (MAIN_HNDLER == null) {
                    MAIN_HNDLER = new Handler(Looper.getMainLooper());
                }
            }
        }
        return MAIN_HNDLER;
    }

    private static ExecutorService getAsyncThreadPool() {
        if (MAIN_HNDLER == null) {
            synchronized (ThreadTool.class) {
                if (MAIN_HNDLER == null) {
                    ASYNC_THREAD_POOL = Executors.newCachedThreadPool();
                }
            }
        }
        return ASYNC_THREAD_POOL;
    }

}
