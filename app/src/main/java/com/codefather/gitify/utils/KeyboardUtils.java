package com.codefather.gitify.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * This class shows/hides soft keyboard.
 * <p>
 * Created by hitesh-lalwani on 6/3/17.
 */

public class KeyboardUtils {

    /**
     * @param focusedView The currently focused view, which would like to receive
     *                    soft keyboard input.
     */

    public static void showSoftKeyboard(Context context, View focusedView) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(focusedView, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
