/*
 * �ļ�����LoadActivity.java
 * ���ܣ�Ӧ�ó�������ʱ�ļ��ؽ��棬�������
 * ���ߣ�huwei
 * ����ʱ�䣺2013-10-22
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
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȥ����Ϣ��
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
				//��¼ʱ�������״̬
//				WebClient.checknetwork(appContext.InwardServerIP,appContext.ExternalServerIP);
				login_activity();
			}
        }.start(); 
    }

    /*
     * ���ܣ�������½����
     * ��������
     * ����ֵ�� ��
     * 
     * */
	public void login_activity()
	{
		/* �½�һ��Intent���� */
		Intent intent = new Intent();
		/* ָ��intentҪ�������� */
		intent.setClass(LoadActivity.this, LoginActivity.class);
		/* ����һ���µ�Activity */
		startActivity(intent);
		/* �رյ�ǰ��Activity */
		LoadActivity.this.finish();	
	}
	protected void onDestroy() 
	{
		super.onDestroy();
	}
}