package com.fydp.utils;

import android.os.Looper;

/**
 * Created by Keith on 14/12/13.
 */
public class Validate {

    public static void notNull(Object val, String variableName) {
        if (val == null) {
            throw new IllegalArgumentException(variableName + " must not be null");
        }
    }

    public static void notEmpty(String val, String variableName) {
        if (val == null || val.length() == 0) {
            throw new IllegalArgumentException(variableName + " must not be empty");
        }
    }

    public static void isMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Method can only be accessed on the applications main thread.");
        }
    }

}
