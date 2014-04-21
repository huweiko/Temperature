/*
 * 文件名：LoadActivity.java
 * 功能：应用程序启动时的加载界面，程序入口
 * 作者：huwei
 * 创建时间：2013-10-22
 * 
 * 
 * 
 * */
package com.refeved.monitor.ui;

import android.os.Bundle;
import android.view.WindowManager;
import com.refeved.monitor.R;
import java.lang.Thread;
import android.content.Intent;

public class LoadActivity extends BaseActivity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
      
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.load_activity);

        new Thread()
        { 
			 public void run()
			 { 	     
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//登录时检查网络状态
//				WebClient.checknetwork(appContext.InwardServerIP,appContext.ExternalServerIP);
				login_activity();
			}
        }.start(); 
    }

    /*
     * 功能：启动登陆界面
     * 参数：无
     * 返回值： 无
     * 
     * */
	public void login_activity()
	{
		/* 新建一个Intent对象 */
		Intent intent = new Intent();
		/* 指定intent要启动的类 */
		intent.setClass(LoadActivity.this, LoginActivity.class);
		/* 启动一个新的Activity */
		startActivity(intent);
		/* 关闭当前的Activity */
		LoadActivity.this.finish();	
	}
	protected void onDestroy() 
	{
		super.onDestroy();
	}
}