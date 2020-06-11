//Code sourced from Link2me at https://link2me.tistory.com/1524

package com.example.siateacher;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class SoftKeyboard extends View {

    private boolean seenKeyboard;
    private OnShownKeyboardListener softKeyboard;
    private OnHiddenKeyboardListener hiddenSoftKeyboard;

    public SoftKeyboard(Context context) {
        this(context, null);
    }

    public SoftKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Activity activity = (Activity)getContext();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        int diffHeight = (screenHeight - statusBarHeight) - h;
        if (diffHeight > 100 && !seenKeyboard) { // Assumes all keyboards are larger than 100px
            seenKeyboard = true;
            onShownSoftKeyboard();
        } else if (diffHeight < 100 && seenKeyboard) {
            seenKeyboard = false;
            onHiddenSoftKeyboard();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void onHiddenSoftKeyboard() {
        if (hiddenSoftKeyboard != null)
            hiddenSoftKeyboard.onHiddenSoftKeyboard();
    }

    public void onShownSoftKeyboard() {
        if (softKeyboard != null)
            softKeyboard.onShowSoftKeyboard();
    }

    public void setOnShownKeyboard(OnShownKeyboardListener listener) {
        softKeyboard = listener;
    }

    public void setOnHiddenKeyboard(OnHiddenKeyboardListener listener) {
        hiddenSoftKeyboard = listener;
    }

    public interface OnShownKeyboardListener {
        public void onShowSoftKeyboard();
    }

    public interface OnHiddenKeyboardListener {
        public void onHiddenSoftKeyboard();
    }
}

