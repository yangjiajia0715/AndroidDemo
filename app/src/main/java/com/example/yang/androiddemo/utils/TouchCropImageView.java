package com.example.yang.androiddemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 底图缩放，浮层不变
 *
 * @author yanglonghui
 */
public class TouchCropImageView extends View {

    //单点触摸的时候
    private float oldX = 0;
    private float oldY = 0;

    //多点触摸的时候
    private float oldx_0 = 0;
    private float oldy_0 = 0;

    private float oldx_1 = 0;
    private float oldy_1 = 0;

    //状态
    private final int STATUS_Touch_SINGLE = 1;//单点
    private final int STATUS_TOUCH_MULTI_START = 2;//多点开始
    private final int STATUS_TOUCH_MULTI_TOUCHING = 3;//多点拖拽中

    private int mStatus = STATUS_Touch_SINGLE;

    //默认的裁剪图片宽度与高度
    private final int defaultCropWidth = 300;
    private final int defaultCropHeight = 300;
    private int cropWidth = defaultCropWidth;
    private int cropHeight = defaultCropHeight;

    protected float oriRationWH = 0;//原始宽高比率
    protected final float maxZoomOut = 5.0f;//最大扩大到多少倍
    protected final float minZoomIn = 0.333333f;//最小缩小到多少倍

    protected Drawable mDrawable;//原图
    protected FloatDrawable mFloatDrawable;//浮层
    protected Rect mDrawableSrc = new Rect();
    protected Rect mDrawableDst = new Rect();
    protected Rect mTempDrawableDst = new Rect();
    protected Rect mDrawableFloat = new Rect();//浮层选择框，就是头像选择框
    protected boolean isFrist = true;

    public void setIsFixBound(boolean fixBound) {
        isFixBound = fixBound;
    }

    public void setOldX(float oldX) {
        this.oldX = oldX;
    }

    protected boolean isFixBound = true;//是否固定高度（比例），2016-7-5 yang
    protected float isFixRate = 2;//是否固定高度（比例），2016-7-5 yang

    protected Context mContext;
    private boolean mIsSingleTouchMode = false;
    private boolean mIsScaleTouchMode = false;

    public TouchCropImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TouchCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TouchCropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);

    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        this.mContext = context;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                this.setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mFloatDrawable = new FloatDrawable(context);//头像选择框
//        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TouchCropImageView);
//        int indexCount = array.getIndexCount();
//        Log.d("yang", "--TouchCropImageView--indexCount=" + indexCount);
//        for (int i = 0; i < array.getIndexCount(); i++) {
//            int index = array.getIndex(i);
//            switch (index) {
//                case R.styleable.TouchCropImageView_cropImageIsFixBount:
//                    isFixBound = array.getBoolean(i, false);
//                    Log.d("yang", "--TouchCropImageView--isFixBound=" + isFixBound);
//                    break;
//                case R.styleable.TouchCropImageView_cropImageRate:
//                    isFixRate = array.getFloat(i, 1);
//                    Log.d("yang", "--TouchCropImageView--isFixRate=" + isFixRate);
//                    break;
//            }
//        }

    }

    public void setDrawable(Drawable mDrawable, int cropWidth, int cropHeight) {
        this.mDrawable = mDrawable;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.isFrist = true;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() > 1) {
            if (mStatus == STATUS_Touch_SINGLE) {
                mStatus = STATUS_TOUCH_MULTI_START;

                oldx_0 = event.getX(0);
                oldy_0 = event.getY(0);

                oldx_1 = event.getX(1);
                oldy_1 = event.getY(1);
            } else if (mStatus == STATUS_TOUCH_MULTI_START) {
                mStatus = STATUS_TOUCH_MULTI_TOUCHING;
            }
        } else {
            if (mStatus == STATUS_TOUCH_MULTI_START || mStatus == STATUS_TOUCH_MULTI_TOUCHING) {
                oldx_0 = 0;
                oldy_0 = 0;

                oldx_1 = 0;
                oldy_1 = 0;

                oldX = event.getX();
                oldY = event.getY();
            }

            mStatus = STATUS_Touch_SINGLE;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = event.getX();
                oldY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                checkBounds();
                mIsScaleTouchMode = false;
                mIsSingleTouchMode = false;
                break;

            case MotionEvent.ACTION_POINTER_1_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_MOVE:
                if (mStatus == STATUS_TOUCH_MULTI_TOUCHING) {
                    mIsScaleTouchMode = false;
                    float newx_0 = event.getX(0);
                    float newy_0 = event.getY(0);

                    float newx_1 = event.getX(1);
                    float newy_1 = event.getY(1);

                    float oldWidth = Math.abs(oldx_1 - oldx_0);
                    float oldHeight = Math.abs(oldy_1 - oldy_0);

                    float newWidth = Math.abs(newx_1 - newx_0);
                    float newHeight = Math.abs(newy_1 - newy_0);

                    boolean isDependHeight = Math.abs(newHeight - oldHeight) > Math.abs(newWidth - oldWidth);

                    float ration = isDependHeight ? ((float) newHeight / (float) oldHeight) : ((float) newWidth / (float) oldWidth);
                    int centerX = mDrawableDst.centerX();
                    int centerY = mDrawableDst.centerY();
                    int _newWidth = (int) (mDrawableDst.width() * ration);
                    int _newHeight = (int) ((float) _newWidth / oriRationWH);

                    float tmpZoomRation = (float) _newWidth / (float) mDrawableSrc.width();
                    if (tmpZoomRation >= maxZoomOut) {
                        _newWidth = (int) (maxZoomOut * mDrawableSrc.width());
                        _newHeight = (int) ((float) _newWidth / oriRationWH);
                    } else if (tmpZoomRation <= minZoomIn) {
                        _newWidth = (int) (minZoomIn * mDrawableSrc.width());
                        _newHeight = (int) ((float) _newWidth / oriRationWH);
                    }


                    mDrawableDst.set(centerX - _newWidth / 2, centerY - _newHeight / 2, centerX + _newWidth / 2, centerY + _newHeight / 2);
                    invalidate();
                    oldx_0 = newx_0;
                    oldy_0 = newy_0;

                    oldx_1 = newx_1;
                    oldy_1 = newy_1;
                } else if (mStatus == STATUS_Touch_SINGLE) {
                    int dx = (int) (event.getX() - oldX);
                    int dy = (int) (event.getY() - oldY);

                    oldX = event.getX();
                    oldY = event.getY();
                    if (!(dx == 0 && dy == 0)) {
                        mIsSingleTouchMode = true;
                        mDrawableDst.offset((int) dx, (int) dy);
                        invalidate();
                    } else {
                        mIsSingleTouchMode = false;
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;
        }

        configureBounds();

        mDrawable.draw(canvas);
        canvas.save();
        canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#a0000000"));
        canvas.restore();
        mFloatDrawable.draw(canvas);
    }


    protected void configureBounds() {
        if (isFrist) {
            oriRationWH = ((float) mDrawable.getIntrinsicWidth()) / ((float) mDrawable.getIntrinsicHeight());

            final float scale = mContext.getResources().getDisplayMetrics().density;
            int w = Math.min(getWidth(), (int) (mDrawable.getIntrinsicWidth() * scale + 0.5f));
            int h = (int) (w / oriRationWH);
            if (getHeight() < getWidth()) {
                int left = (getWidth() - w) / 2;
//                int top = (ClientApplication.getsScreenHeight() - h) / 2;
                int top = (1920 - h) / 2;
                int right = left + w;
                int bottom = top + h;

                mDrawableSrc.set(left, top, right, bottom);
                mDrawableDst.set(mDrawableSrc);
            } else {
                int left = (getWidth() - w) / 2;
                int top = (getHeight() - h) / 2;
                int right = left + w;
                int bottom = top + h;

                mDrawableSrc.set(left, top, right, bottom);
                mDrawableDst.set(mDrawableSrc);
            }

            int floatWidth = cropWidth;//dipTopx(mContext, cropWidth);
            int floatHeight = cropHeight;//dipTopx(mContext, cropHeight);
            if (floatWidth > getWidth()) {
                floatWidth = getWidth();
                //floatHeight=cropHeight*floatWidth/cropWidth;
            }
            floatHeight = floatWidth;
            if (floatHeight > getHeight()) {
                floatHeight = getHeight();
                //floatWidth=cropWidth*floatHeight/cropHeight;
            }

            if (getHeight() < getWidth()) {
                int floatLeft = (getWidth() - floatWidth) / 2;
//                int floatTop = (ClientApplication.getsScreenHeight() - floatHeight) / 2;
                int floatTop = (1920 - floatHeight) / 2;
                Log.d("yang", "--TouchCropImageView-1-floatLeft=" + floatLeft + ",floatTop=" + floatTop);
                mDrawableFloat.set(floatLeft, floatTop, floatLeft + floatWidth, floatTop + floatHeight);
            } else {
                int floatLeft = (getWidth() - floatWidth) / 2;
                int floatTop = (getHeight() - floatHeight) / 2;
                Log.d("yang", "--TouchCropImageView--floatLeft=" + floatLeft + ",floatTop=" + floatTop);
                mDrawableFloat.set(floatLeft, floatTop, floatLeft + floatWidth, floatTop + floatHeight);
            }


            Log.d("yang", "--TouchCropImageView-3-isFixBound=" + isFixBound + ",isFixRate=" + isFixRate);
            if (isFixBound) {
                int floatLeft = (getWidth() - floatWidth) / 2;
//				int floatTop = (getHeight()-floatHeight)/2;
//                cropHeight = Utility.getScreenWidth(getContext())/2;//getCropImage方法中调用，保持一致
                //按标注上来153dp
                cropHeight = 153;//getCropImage方法中调用，保持一致
                int floatTop = (getHeight() - cropHeight) / 2;
//				mDrawableFloat.set(floatLeft, floatTop,floatLeft+floatWidth, floatTop+floatHeight);
                mDrawableFloat.set(floatLeft, floatTop, floatLeft + floatWidth, floatTop + cropHeight);//保持一致
            }


            isFrist = false;
        }

        mDrawable.setBounds(mDrawableDst);
        mFloatDrawable.setBounds(mDrawableFloat);
    }

    protected void checkBounds() {
        if (mIsSingleTouchMode) {
            /*if(mDrawableSrc.width() < mDrawableDst.width() || mDrawableSrc.height() < mDrawableDst.height()){
                mDrawableDst.set(mTempDrawableDst);
				invalidate();
				return;
			}else{
				mDrawableDst.set(mDrawableSrc);
				mDrawableDst.set(mDrawableDst);
				invalidate();
				return;
			}*/
            int newLeft = mDrawableDst.left;
            int newTop = mDrawableDst.top;

            boolean isChange = false;
            if (mDrawableDst.left < -mDrawableDst.width()) {
                newLeft = -mDrawableDst.width();
                isChange = true;
            }

            if (mDrawableDst.top < -mDrawableDst.height()) {
                newTop = -mDrawableDst.height();
                isChange = true;
            }

            if (mDrawableDst.left > getWidth()) {
                newLeft = getWidth();
                isChange = true;
            }

            if (mDrawableDst.top > getHeight()) {
                newTop = getHeight();
                isChange = true;
            }

            mDrawableDst.offsetTo(newLeft, newTop);
            if (isChange) {
                invalidate();
            }
        } else {
            if (mDrawableSrc.width() < mDrawableDst.width() || mDrawableSrc.height() < mDrawableDst.height()) {
            } else {
                mDrawableDst.set(mDrawableSrc);
                invalidate();
                return;
            }
            int newLeft = mDrawableDst.left;
            int newTop = mDrawableDst.top;

            boolean isChange = false;
            if (mDrawableDst.left < -mDrawableDst.width()) {
                newLeft = -mDrawableDst.width();
                isChange = true;
            }

            if (mDrawableDst.top < -mDrawableDst.height()) {
                newTop = -mDrawableDst.height();
                isChange = true;
            }

            if (mDrawableDst.left > getWidth()) {
                newLeft = getWidth();
                isChange = true;
            }

            if (mDrawableDst.top > getHeight()) {
                newTop = getHeight();
                isChange = true;
            }

            mDrawableDst.offsetTo(newLeft, newTop);
            mTempDrawableDst.set(mDrawableDst);
            if (isChange) {
                invalidate();
            }
        }
    }

    public Bitmap getCropImage() {
        Bitmap tmpBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.RGB_565);
        Canvas canvas = new Canvas(tmpBitmap);
        mDrawable.draw(canvas);

		/*Matrix matrix=new Matrix();
        float scale=(float)(mDrawableSrc.width())/(float)(mDrawableDst.width());
		matrix.postScale(scale, scale);
		
	    Bitmap ret = Bitmap.createBitmap(tmpBitmap, mDrawableFloat.left, mDrawableFloat.top, mDrawableFloat.width(), mDrawableFloat.height(), matrix, true);
	    */
        final Rect displayedImageRect = ImageViewUtil.getBitmapRectCenterInside(tmpBitmap, this);
        final float actualImageWidth = tmpBitmap.getWidth();
        final float displayedImageWidth = displayedImageRect.width();
        final float scaleFactorWidth = actualImageWidth / displayedImageWidth;

        final float actualImageHeight = tmpBitmap.getHeight();
        final float displayedImageHeight = displayedImageRect.height();
        final float scaleFactorHeight = actualImageHeight / displayedImageHeight;
        // Get crop window position relative to the displayed image.
        final float cropWindowX = mDrawableFloat.left - displayedImageRect.left;
        final float cropWindowY = mDrawableFloat.top - displayedImageRect.top;
        final float cropWindowWidth = mDrawableFloat.width();
        final float cropWindowHeight = mDrawableFloat.height();
        // Scale the crop window position to the actual size of the Bitmap.
        float actualCropX = cropWindowX * scaleFactorWidth;
        float actualCropY = cropWindowY * scaleFactorHeight;
        float actualCropWidth = cropWindowWidth * scaleFactorWidth;
        float actualCropHeight = cropWindowHeight * scaleFactorHeight;
        if (actualCropX < 0) {
            actualCropWidth += actualCropX;
            actualCropX = 0;
        }

        if (actualCropY < 0) {
            actualCropHeight += actualCropY;
            actualCropY = 0;
        }

        if (actualCropX + actualCropWidth > tmpBitmap.getWidth()) {
            actualCropWidth = tmpBitmap.getWidth() - actualCropX;
        }

        if (actualCropY + actualCropHeight > tmpBitmap.getHeight()) {
            actualCropHeight = tmpBitmap.getHeight() - actualCropY;
        }

        Bitmap ret = Bitmap.createBitmap(tmpBitmap,
                (int) actualCropX,
                (int) actualCropY,
                (int) actualCropWidth,
                (int) actualCropHeight);
        tmpBitmap.recycle();
        tmpBitmap = null;

        Log.d("yang", "--TouchCropImageView--k-cropWidth=" + cropWidth + ",cropHeight=" + cropHeight);

        Bitmap newRet = Bitmap.createScaledBitmap(ret, cropWidth, cropHeight, false);
        ret.recycle();
        ret = newRet;

        return ret;
    }

    public int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
