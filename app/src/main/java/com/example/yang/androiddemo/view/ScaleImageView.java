package com.example.yang.androiddemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
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

    private  float mInitScale;
    private  float mMaxScale;
    private  float mMidScale;

    private Matrix mMatrix;

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
        mMatrix = new Matrix();
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
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = 1.0f;

            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }

            if (dh > height && dw < width) {
                scale = height * 1.0f / dh;
            }

            if ((dw > width && dh > height) || (dw < width && dh < height)) {
                scale = Math.min(width * 1.0f / dw,height * 1.0f /dh);
            }

            Log.d(TAG, "onGlobalLayout: scale=" + scale);
            mInitScale = scale;
            mMidScale = scale * 2;
            mMaxScale = scale * 4;

            int dx = width / 2 - dw / 2;
            int dy = height / 2 - dh / 2;

            mMatrix.postTranslate(dx,dy);
            mMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);

            setImageMatrix(mMatrix);

            mOnce =false;
        }
    }
}
