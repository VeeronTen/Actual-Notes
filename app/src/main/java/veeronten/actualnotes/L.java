package veeronten.actualnotes;

import android.text.TextUtils;

public final class L {
    public static String DEFAULT_TAG="Actual Notes";

    private L() {}

    public static void printStackTrace(Throwable t) {
        if (BuildConfig.DEBUG)
            L.printStackTrace(t);
    }

    public static int v(String msg) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.v(DEFAULT_TAG, getLocation()+msg);
    }

    public static int v(String msg, Throwable tr) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.v(DEFAULT_TAG, getLocation()+msg, tr);
    }

    public static int d(String msg) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.d(DEFAULT_TAG, getLocation()+msg);
    }

    public static int d(String msg, Throwable tr) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.d(DEFAULT_TAG, getLocation()+msg, tr);
    }

    public static int i(String msg) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.i(DEFAULT_TAG, getLocation()+msg);
    }

    public static int i(String msg, Throwable tr) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.i(DEFAULT_TAG, getLocation()+msg, tr);
    }

    public static int w(String msg) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.w(DEFAULT_TAG, getLocation()+msg);
    }

    public static int w(String msg, Throwable tr) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.w(DEFAULT_TAG, getLocation()+msg, tr);
    }

    public static int w(Throwable tr) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.w(DEFAULT_TAG, tr);
    }

    public static int e(String msg) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.e(DEFAULT_TAG, getLocation()+msg);
    }

    public static int e(String msg, Throwable tr) {
        if (!BuildConfig.DEBUG)
            return 0;
        return android.util.Log.e(DEFAULT_TAG, getLocation()+msg, tr);
    }

    private static String getLocation() {
        final String className = L.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];

            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                }
                else if (trace.getClassName().startsWith(className)) {
                    found = true;
                    continue;
                }
            }
            catch (ClassNotFoundException e) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }
}