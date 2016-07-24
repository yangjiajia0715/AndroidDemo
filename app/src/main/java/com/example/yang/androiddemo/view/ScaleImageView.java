package com.example.yang.androiddemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/7/24 0024.
 */
public class ScaleImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "ScaleImageView";
    private boolean mOnce = true;

    private Context mContext;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setScaleType(ScaleType.MATRIX);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        Log.d(TAG, "init: ");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow: ");
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow: ");
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        Log.d(TAG, "onGlobalLayout: ");
        if (mOnce) {
            //控件的宽和高
            int width = getWidth();
            int height = getHeight();
            //图片的宽和高
            Drawable d = getDrawable();
            if (d==null) {
                return;
            }

            float scale = 1.0f;

            mOnce =false;
        }
    }
}
