package com.refeved.monitor.util;

import android.app.Activity; 
import android.content.Context; 
import android.graphics.Canvas; 
import android.graphics.Color; 
import android.graphics.Paint; 
import android.graphics.Typeface; 
import android.os.Bundle; 
import android.view.View; 

/**
 * 
*    
* 项目名称：mip   
* 类名称：Typefaces   
* 类描述： 字体样式
* 创建人：JIE
* 创建时间：2011-12-29 上午11:51:05   
* 修改人：Administrator   
* 修改时间：2011-12-29 上午11:51:05   
* 修改备注：   
* @version    
*
 */
public class Typefaces extends Activity 
{ 
	/** Called when the activity is first created. */ 
	@Override 
	public void onCreate(Bundle savedInstanceState) 
	{ 
		super.onCreate(savedInstanceState); 
	
		setContentView(new SampleView(this)); 
	} 
	
	private static class SampleView extends View 
	{ 
		private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		private Typeface mFace; 
	
		public SampleView(Context context) 
		{
			super(context); 
			//实例化自定义字体 
			mFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/Clockopia.ttf"); 
			//设置字体大小 
			mPaint.setTextSize(18); 
		} 
		
		@Override 
		protected void onDraw(Canvas canvas) 
		{
			canvas.drawColor(Color.WHITE); 
//			//绘制默认字体 
//			mPaint.setTypeface(null); 
//			canvas.drawText("Default:abcdefg", 10, 100, mPaint); 
			//绘制自定义字体 
			mPaint.setTypeface(mFace); 
			canvas.drawText("Custom:abcdefg", 10, 200, mPaint); 
		} 
	}
}