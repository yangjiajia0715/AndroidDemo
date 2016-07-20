package com.example.yang.androiddemo.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.example.yang.androiddemo.R;

/**
 * 头像图片选择框的浮层
 * @author Administrator
 *
 */
public class FloatDrawable extends Drawable {
	
	private Context mContext;
	//private Drawable mCropPointDrawable;
	private Drawable mDrawableLeft;
	private Drawable mDrawableTop;
	private Drawable mDrawableRight;
	private Drawable mDrawableBottom;
	private Paint mLinePaint=new Paint();
	{
	    mLinePaint.setARGB(200, 50, 50, 50);
	    mLinePaint.setStrokeWidth(1F);
	    mLinePaint.setStyle(Paint.Style.STROKE);
	    mLinePaint.setAntiAlias(true);
	    mLinePaint.setColor(Color.WHITE);
	}
	
	public FloatDrawable(Context context) {
		super();
		this.mContext=context;
		init();
	}
	
	private void init()
	{
		//mCropPointDrawable=mContext.getResources().getDrawable(R.drawable.clip_point);
		mDrawableLeft = mContext.getResources().getDrawable(R.mipmap.clip_left);
		mDrawableTop = mContext.getResources().getDrawable(R.mipmap.clip_top);
		mDrawableRight = mContext.getResources().getDrawable(R.mipmap.clip_right);
		mDrawableBottom = mContext.getResources().getDrawable(R.mipmap.clip_bottom);
	}
	
	public int getCirleWidth()
	{
		return mDrawableLeft.getIntrinsicWidth() * 2;
	}
	
	public int getCirleHeight()
	{
		return mDrawableLeft.getIntrinsicHeight() * 2;
	}

	@Override
	public void draw(Canvas canvas) {
		
		int left=getBounds().left;
		int top=getBounds().top;
		int right=getBounds().right;
		int bottom=getBounds().bottom;
		
		Rect mRect=new Rect(
				left+mDrawableLeft.getIntrinsicWidth()/2, 
				top+mDrawableLeft.getIntrinsicHeight()/2, 
				right-mDrawableLeft.getIntrinsicWidth()/2, 
				bottom-mDrawableLeft.getIntrinsicHeight()/2);
		//方框
		canvas.drawRect(mRect, mLinePaint);
		
		//左上
		mDrawableLeft.setBounds(left + mDrawableLeft.getIntrinsicWidth()/2, top + 
				mDrawableLeft.getIntrinsicHeight()/2, left+ mDrawableLeft.getIntrinsicWidth() + 
				mDrawableLeft.getIntrinsicWidth() / 2, top+ mDrawableLeft.getIntrinsicHeight() + 
				mDrawableLeft.getIntrinsicWidth() / 2);
		mDrawableLeft.draw(canvas);
		
		//右上
		mDrawableTop.setBounds(right-mDrawableTop.getIntrinsicWidth() - mDrawableTop.getIntrinsicWidth() / 2,
				top + mDrawableTop.getIntrinsicWidth() / 2, right - mDrawableTop.getIntrinsicWidth() / 2, 
				top+mDrawableTop.getIntrinsicHeight() + mDrawableTop.getIntrinsicWidth() / 2);
		mDrawableTop.draw(canvas);
		
		//左下
		mDrawableBottom.setBounds(left + mDrawableTop.getIntrinsicWidth() / 2, bottom-mDrawableBottom.getIntrinsicHeight() - mDrawableBottom.getIntrinsicHeight() / 2, 
				left+mDrawableBottom.getIntrinsicWidth() + mDrawableTop.getIntrinsicWidth() / 2, bottom - mDrawableBottom.getIntrinsicHeight() / 2);
		mDrawableBottom.draw(canvas);
		
		//右下
		mDrawableRight.setBounds(right-mDrawableRight.getIntrinsicWidth() - mDrawableRight.getIntrinsicWidth() / 2, 
				bottom-mDrawableRight.getIntrinsicHeight() - mDrawableRight.getIntrinsicHeight() / 2, 
				right - mDrawableRight.getIntrinsicWidth() / 2, bottom - mDrawableRight.getIntrinsicHeight() / 2);
		mDrawableRight.draw(canvas);
		
	}

	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(new Rect(
				bounds.left-mDrawableLeft.getIntrinsicWidth()/2, 
				bounds.top-mDrawableLeft.getIntrinsicHeight()/2, 
				bounds.right+mDrawableLeft.getIntrinsicWidth()/2, 
				bounds.bottom+mDrawableLeft.getIntrinsicHeight()/2));
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

}
