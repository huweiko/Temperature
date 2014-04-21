/*
 * 文件名：MainActivity.java
 * 功能：登陆后的主界面，程序入口
 * 作者：huwei
 * 创建时间：2013-10-17
 * 
 * 
 * 
 * */
package com.refeved.monitor.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.AppManager;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.net.BackgroundService;
import com.refeved.monitor.ui.PreferenceListFragment.OnPreferenceAttachedListener;

public class MainActivity extends BaseActivity implements
		OnPreferenceAttachedListener, OnClickListener {
	//程序退出时执行，用于计算两次按下返回键的时间间隔（如果2秒之内再按下退出键的话就退出应用程序）
	class CheckQuitThread extends Thread {
		@Override
		public void run() {
			int count = 2;
			while (count-- > 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*检查是否断线，如果断线弹出提示框*/
	class CheckNetConnectThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(2000);
					//检查网络连接
					if(!checkNetwork()){
						Message message=new Message();  
						message.what=1;  
						mHandler.sendMessage(message);  
						
					}
					else{
						Message message=new Message();  
						message.what=2;  
						mHandler.sendMessage(message);  
						
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@SuppressLint("HandlerLeak")
	public Handler mHandler=new Handler()  
	{  
		public void handleMessage(Message msg)  
		{  
			switch(msg.what)  
			{  
			case 1:  
				if(mNotNetWorking.getVisibility() == View.GONE)
					mNotNetWorking.setVisibility(View.VISIBLE);
				break;  
			case 2:  
				if(mNotNetWorking.getVisibility() == View.VISIBLE)
					mNotNetWorking.setVisibility(View.GONE);
				break;  
			default:  
				break;            
			}  
			super.handleMessage(msg);  
		}  
	};
	
	private  final int CLICK_SURVEIL = 1;
	private  final int CLICK_LOG = 2;
	private  final int CLICK_SETTINGS = 3;
	private View mNotNetWorking;
	CheckQuitThread checkQuitThread;
	private AppContext appContext;
	private CheckNetConnectThread checkNetThread;
	Intent jPushServiceIntent;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;//ViewPager用于实现多页面的切换效果（也就是左右滑动效果）

	View mCustomView;//定制界面
	View mCustomMainActivityView;//定制界面
	ImageButton mHeaderRefresh;    //监控界面刷新按钮
	ImageButton mHeaderLogRefresh; //日子界面刷新按钮
	ImageButton mHeaderLocation;   //地址界面按钮
	TextView mHeaderTittle;        //标题头
	TextView mHeaderTextCount;        //刷新计数文本
	ProgressBar mHeaderProgressBarRefresh; //计数刷新进度条
	ImageButton mImageButtonVersionInfo; //计数刷新进度条
	ImageButton mHeaderHome;       //回到主页的按钮
	Button m_log;
	static ImageButton mSurveilMenu;    //menu的监控按钮
	static ImageButton mLogMenu; 		//menu的日志按钮
	static ImageButton mSettingsMenu;   //menu的设置按钮
	

	ImageView	imageview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);//启动主界面monitor
		appContext = (AppContext) getApplication();

		//得到我们要显示的xml文件
		mCustomView = getLayoutInflater().inflate(R.layout.custom_actionbar,
				null);
		mCustomMainActivityView = getLayoutInflater().inflate(R.layout.activity_main,
				null);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		//将取出来的xml文件装载进ActionBar
		actionBar.setCustomView(mCustomView, new ActionBar.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient_bg));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		mHeaderRefresh = (ImageButton) mCustomView
				.findViewById(R.id.main_head_refresh);
		mHeaderLocation = (ImageButton) mCustomView
				.findViewById(R.id.main_head_location);
		mHeaderLogRefresh = (ImageButton) mCustomView
				.findViewById(R.id.main_head_log_refresh);
		mHeaderTextCount = (TextView) mCustomView
				.findViewById(R.id.main_head_textview_count);
		mHeaderProgressBarRefresh = (ProgressBar) mCustomView
				.findViewById(R.id.main_head_progressBar_refresh);
//屏蔽版本升级按钮
				mImageButtonVersionInfo = (ImageButton) mCustomView
				.findViewById(R.id.main_version_info);
		mImageButtonVersionInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DevVersionInfoActivity.class);
				startActivity(intent);
			}
		});
		//网络断线提示
		mNotNetWorking =  findViewById(R.id.notnetworking);
		mNotNetWorking.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
	            startActivity(intent);
			}
		});
		
		checkNetThread = new CheckNetConnectThread();
		checkNetThread.start();
		
		m_log = (Button) mCustomView
				.findViewById(R.id.main_head_log_switch);

		mHeaderHome = (ImageButton) mCustomView
				.findViewById(R.id.main_head_home);
		mHeaderHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//跳到第一个页面
				mViewPager.setCurrentItem(0);
			}
		});

		mSurveilMenu = (ImageButton) findViewById(R.id.menuSurveril);
		mLogMenu = (ImageButton) findViewById(R.id.menuLog);
		mSettingsMenu = (ImageButton) findViewById(R.id.menuSetting);
		
		mSurveilMenu.setOnClickListener(this);  
		mSurveilMenu.setTag(CLICK_SURVEIL);  
		
		mLogMenu.setOnClickListener(this);  
		mLogMenu.setTag(CLICK_LOG); 
		
		mSettingsMenu.setOnClickListener(this);  
		mSettingsMenu.setTag(CLICK_SETTINGS); 
		
		mHeaderTittle = (TextView) mCustomView.findViewById(R.id.main_head_title);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);//指定3个页面，为了保存3个页面的状态
		mViewPager.setAdapter(mSectionsPagerAdapter);
		//当页面发生改变的时候（比如翻滚）会进入此函数
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {

						switch (position) {
						case 0:
							mHeaderHome.setVisibility(View.VISIBLE);
							mHeaderRefresh.setVisibility(View.VISIBLE);
							mHeaderLocation.setVisibility(View.VISIBLE);
							mHeaderLogRefresh.setVisibility(View.INVISIBLE);
							m_log.setVisibility(View.GONE);
							mHeaderTittle
									.setText(R.string.header_tittle_surveil);
							mSurveilMenu.setImageResource(R.drawable.menu_surveil_on);
							mLogMenu.setImageResource(R.drawable.menu_log);
							mSettingsMenu.setImageResource(R.drawable.menu_settings);
							mImageButtonVersionInfo.setVisibility(View.INVISIBLE);
							break;
						case 1:
							mHeaderHome.setVisibility(View.VISIBLE);
							mHeaderRefresh.setVisibility(View.GONE);
							mHeaderLocation.setVisibility(View.GONE);
							mHeaderLogRefresh.setVisibility(View.INVISIBLE);
							mHeaderTittle.setText(R.string.header_tittle_log);
							mHeaderProgressBarRefresh.setVisibility(View.INVISIBLE);
							mHeaderTextCount.setVisibility(View.INVISIBLE);
							m_log.setVisibility(View.VISIBLE);
							mSurveilMenu.setImageResource(R.drawable.menu_surveil);
							mLogMenu.setImageResource(R.drawable.menu_log_on);
							mSettingsMenu.setImageResource(R.drawable.menu_settings);
							mImageButtonVersionInfo.setVisibility(View.INVISIBLE);
							break;
						case 2:
							mHeaderHome.setVisibility(View.VISIBLE);
							mHeaderRefresh.setVisibility(View.GONE);
							mHeaderRefresh.setVisibility(View.GONE);
							mHeaderLocation.setVisibility(View.GONE);
							mHeaderLogRefresh.setVisibility(View.INVISIBLE);
							mHeaderProgressBarRefresh.setVisibility(View.INVISIBLE);
							mHeaderTextCount.setVisibility(View.INVISIBLE);
							m_log.setVisibility(View.GONE);
							mHeaderTittle
									.setText(R.string.header_tittle_settings);
							mSurveilMenu.setImageResource(R.drawable.menu_surveil);
							mLogMenu.setImageResource(R.drawable.menu_log);
							mSettingsMenu.setImageResource(R.drawable.menu_settings_on);
							mImageButtonVersionInfo.setVisibility(View.VISIBLE);
							break;

						default:
							break;
						}
					}
				});

		checkQuitThread = null;

		if (appContext.SettingsNotification) {
			if (appContext.BackgroundServiceIntent == null) {
				appContext.BackgroundServiceIntent = new Intent(appContext,
						BackgroundService.class);
				appContext.startService(appContext.BackgroundServiceIntent);
			}
		}

	}

	public void onClick(View v)
	{
		int tag = (Integer) v.getTag();
		switch (tag)
		{
			case CLICK_SURVEIL:
				mViewPager.setCurrentItem(0);break;
			case CLICK_LOG:
				mViewPager.setCurrentItem(1);break;
			case CLICK_SETTINGS:
				mViewPager.setCurrentItem(2);break;
			default:break;
		}
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (!(appContext.SettingsNotification && appContext.SettingsRunInBackGround)) {
			if (appContext.BackgroundServiceIntent != null) {
				stopService(appContext.BackgroundServiceIntent);
				appContext.BackgroundServiceIntent = null;
			}
		}

	}
	/*
	 * 按键按下的处理函数
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//连续按退出键两次就退出程序
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (checkQuitThread == null || !checkQuitThread.isAlive()) {
				UIHealper.DisplayToast(appContext,
						"Click the back key again to quit .");
				checkQuitThread = new CheckQuitThread();
				checkQuitThread.start();
			} else {
				AppManager.getAppManager().AppExit(this);
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

//		 HomeSectionFragment mHomeFragment ;
		SurveilSectionFragment mSurveilFragment;
		LogSectionFragment mLogFragment;
		SettingsSectionFragment mSettingsFragment;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);

//			 mHomeFragment = null;
			mSurveilFragment = null;
			mLogFragment = null;
			mSettingsFragment = null;

		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (mSurveilFragment == null)
					mSurveilFragment = new SurveilSectionFragment();
				return mSurveilFragment;
			case 1:
				if (mLogFragment == null)
					mLogFragment = new LogSectionFragment();
				return mLogFragment;
			case 2:
				if (mSettingsFragment == null)
					mSettingsFragment = new SettingsSectionFragment();
				return mSettingsFragment;
			}

			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}
		//返回页面标题
		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.menu_surviel);
			case 1:
				return getString(R.string.menu_search);
			case 2:
				return getString(R.string.menu_settings);
			}
			return null;
		}
	}

	@Override
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {

	}
	
	/*检查网络连接状态*/
	private boolean checkNetwork() {
        ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = conn.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            return true;
        }
        return false;
    }
}
